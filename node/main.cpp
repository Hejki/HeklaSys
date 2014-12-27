#include <EtherCard.h>
#include <MCP79412RTC.h>

#include "common.h"
#include "hs_config.h"
#include "HSMessage.h"

byte Ethernet::buffer[HS_ETHERNET_BUF_SIZE];
DeviceState status;

bool hs_setup();
void hs_loop();
void hs_udpReceive(word port, byte ip[4], const char *data, word len);
bool hs_checkSendInterval();
void hs_processPins();
void hs_switch(HSPinConf *pin);

int main(void) {
	init();

    if (!hs_setup()) {
//        dbg("Init fail");
        return 0;
    }
    
	for (;;)
		hs_loop();

	return 0;
}

bool hs_setup() {
#ifdef DEBUG
    Debug.begin();
#endif
    
#ifdef NO_RTC
    byte macAddress[] = HS_MAC_ADDRESS;
    if (ether.begin(sizeof Ethernet::buffer, macAddress, NET_CS_PIN) == 0) {
//        dbg("Cannot init ether card with static MAC address.");
        return false;
    }
#else
    // RTC
    if (!RTC.isRunning()) {
//        dbg("RTC was not running set default time");
        setTime(0, 0, 0, 1, 1, 0); // 1.1.2000 00:00:00
        RTC.set(now());
        RTC.vbaten(true);
    }

    setSyncProvider(RTC.get);
    if (timeStatus() != timeSet) {
//        dbg("Unable to sync with the RTC");
        return false;
    }

    // MAC address and Ethernet setup
    uint8_t rtcId[8];
    RTC.idRead(rtcId);

    if (ether.begin(sizeof Ethernet::buffer, rtcId, NET_CS_PIN) == 0) {
//        dbg("Cannot init ether card with MAC: %x:%x:%x:%x:%x:%x",
//            rtcId[0], rtcId[1], rtcId[2], rtcId[3], rtcId[4], rtcId[5], rtcId[6]);
        return false;
    }
#endif
    
    if (!ether.dhcpSetup()) {
        return false;
    }
    
//    dbg("IP: %d.%d.%d.%d", ether.myip[0], ether.myip[1], ether.myip[2], ether.myip[3]);
    
    ether.udpServerListenOnPort(&hs_udpReceive, HS_DEF_UDP_SERVER_PORT);
    
    hs_config_init();
    status.lastSendTime = 0;
    
    return true;
}

void hs_loop() {
    ether.packetLoop(ether.packetReceive());
    
    if (!status.settingsMode) {
        hs_processPins();
    }
}


void hs_udpReceive(word port, byte ip[4], const char *data, word msgLength) {
//    dbg("%d.%d.%d.%d:%d, len: %d", ip[0], ip[1], ip[2], ip[3], port, msgLength);
    
    uint8_t messageLength = (msgLength & 0xFF);
    if (messageLength >= 3) {
        HSMessage *message = NULL;
        uint8_t type = data[0];
        uint8_t length = data[1];
        uint8_t id = data[2];
        
        if (messageLength != (length + 4)) {
            message = new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
        }
        
        if (!message) {
            uint8_t crc = data[messageLength - 1];
        
//            dbg("t: %d, l: %d, id: %d, crc: %d", type, length, id, crc);
            message = HSMessage::createMessage(type, length, id, crc, data);
        }
        
        if (message) {
            message->send(ip);
            delete message;
        }
    }
}

bool hs_checkSendInterval() {
    time_t lastSendDelay = now() - status.lastSendTime;
    
    if (hs_node_config.statusSendTimeUnit == HS_CONFIG_TIME_UNIT_MINUTE) {
        lastSendDelay /= 60;
    } else if (hs_node_config.statusSendTimeUnit == HS_CONFIG_TIME_UNIT_HOUR) {
        lastSendDelay /= 3600;
    }
    
    return hs_node_config.statusSendInterval > 0 && lastSendDelay >= hs_node_config.statusSendInterval;
}

void hs_processPins() {
    bool check = hs_checkSendInterval();
    
//    dbg("si: %d, u: %d, p: %d, lt: %ld", hs_node_config.statusSendInterval, hs_node_config.statusSendTimeUnit, hs_node_config.numberOfPinPositions, status.lastSendTime);
    
    for (uint8_t i = 0; i < hs_node_config.numberOfPinPositions; i++) {
        HSPinConf pin = hs_pin_configurations[i];
        HSMessage *message = NULL;
        
//        if (pin.type != 0) {
//            dbg("n: %d, t: %d, c: %d", pin.number, pin.type, pin.config);
//        }
        
        switch (pin.type) {
            case HS_CONFIG_PIN_TYPE_READ_D:
            case HS_CONFIG_PIN_TYPE_READ_A:
            case HS_CONFIG_PIN_TYPE_TEMP:
                if (check) {
                    message = new HSValueMessage(i, hs_config_get_pin_value(i));
                }
                break;
                
            case HS_CONFIG_PIN_TYPE_SWITCH:
                hs_switch(&pin);
                break;
        }
        
        if (message) {
            message->send(NULL);
            delete message;
        }
    }
    
    if (check) {
        status.lastSendTime = now();
    }
}

void hs_switch(HSPinConf *pin) {
    uint8_t dependPin = pin->config & 0xF;
    uint8_t switchHigh = (pin->config & 0x10) >> 4;
    uint8_t switchOn = digitalRead(pin->number);
    
    digitalWrite(dependPin, switchHigh == switchOn);
}