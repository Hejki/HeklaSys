#include <EtherCard.h>
#include <MCP79412RTC.h>

#include "common.h"
#include "hs_config.h"
#include "HSMessage.h"

byte Ethernet::buffer[HS_ETHERNET_BUF_SIZE];

uint16_t udpLocalPort = HS_DEF_UDP_LOCAL_PORT;
uint16_t udpRemotePort = HS_DEF_UDP_SERVER_PORT;
uint8_t  udpRemoteIp[4] = HS_DEF_UDP_SERVER_IP;
DeviceState status;
HSConfiguration config;

bool hs_setup();
void hs_loop();
void hs_udpReceive(word port, byte ip[4], const char *data, word len);
bool hs_checkSendInterval();
void hs_processPins();
float hs_read_temperature(uint8_t pin);
float hs_read_humidity(uint8_t pin);

int main(void) {
	init();

    if (!hs_setup()) {
        dbg("Init fail");
        return 0;
    }
    
	for (;;)
		hs_loop();

	return 0;
}

bool hs_setup() {
    Debug.begin();
    
#if NO_RTC
    byte macAddress[] = HS_MAC_ADDRESS;
    if (ether.begin(sizeof Ethernet::buffer, macAddress) == 0) {
        LOG("Cannot init ether card with static MAC address.");
        return false;
    }
#else
    // RTC
    if (!RTC.isRunning()) {
        dbg("RTC was not running set default time");
        setTime(0, 0, 0, 1, 1, 0); // 1.1.2000 00:00:00
        RTC.set(now());
        RTC.vbaten(true);
    }
    
    setSyncProvider(RTC.get);
    if (timeStatus() != timeSet) {
        dbg("Unable to sync with the RTC");
        return false;
    }
    
    // MAC address and Ethernet setup
    uint8_t rtcId[8];
    RTC.idRead(rtcId);
    if (ether.begin(sizeof Ethernet::buffer, rtcId) == 0) {
        dbg("Cannot init ether card with MAC: %x:%x:%x:%x:%x:%x",
            rtcId[0], rtcId[1], rtcId[2], rtcId[3], rtcId[4], rtcId[5], rtcId[6]);
        return false;
    }
#endif
    
    if (!ether.dhcpSetup()) {
        dbg("DHCP failed.");
    } else {
        dbg("IP: %d.%d.%d.%d", ether.myip[0], ether.myip[1], ether.myip[2], ether.myip[3]);
    }
    
    ether.udpServerListenOnPort(&hs_udpReceive, udpLocalPort);
    
    hs_config_init(&config);
    dbg("Config: %d, %d, %d", config.statusSendInterval, config.statusSendTimeUnit, config.numberOfPinPositions);
    
    return true;
}

void hs_loop() {
//    ether.packetLoop(ether.packetReceive());
    hs_processPins();
}

void hs_udpReceive(word port, byte ip[4], const char *data, word msgLength) {
    dbg("%d.%d.%d.%d:%d, len: %d", ip[0], ip[1], ip[2], ip[3], port, msgLength);
    
    uint8_t messageLength = (msgLength & 0xFF);
    if (messageLength >= 3) {
        uint8_t type = data[0];
        uint8_t length = data[1];
        uint8_t id = data[2];
        
        if (messageLength != (length + 4)) {
            // error message bad length
            dbg("Bad message length, expect (%d + 4) but was %d", length, messageLength);
            return;
        }
        
        uint8_t crc = data[messageLength - 1];
        
        // Info
        dbg("t: %d, l: %d, id: %d, crc: %d", type, length, id, crc);
        
        HSMessage *message = HSMessage::createMessage(type, length, id, crc, data);
        
        message->process();
        ether.sendUdp(message->getReply(), message->getReplySize(), udpLocalPort, udpRemoteIp, udpRemotePort);
        
        delete message;
    }
}

bool hs_checkSendInterval() {
    time_t lastSendDelay = now() - status.lastSendTime;
    if (config.statusSendTimeUnit == HS_CONFIG_TIME_UNIT_MINUTE) {
        lastSendDelay /= 60;
    } else if (config.statusSendTimeUnit == HS_CONFIG_TIME_UNIT_HOUR) {
        lastSendDelay /= 3600;
    }
    
    return config.statusSendInterval > 0 && lastSendDelay > config.statusSendInterval;
}

void hs_processPins() {
    bool check = hs_checkSendInterval();
    
    dbg("Process pins (conf: %d, %d, %d, %d)", config.statusSendInterval, config.statusSendTimeUnit, config.numberOfPinPositions, check);
    for (uint8_t i = 0; i < config.numberOfPinPositions; i++) {
        HSPinConf pin;
        
        hs_config_read_pin(i, &pin);
        dbg("Pin: %d, type: %d", pin.number, pin.type);
        
        switch (pin.type) {
            case HS_CONFIG_PIN_TYPE_READ_D:
                dbg("- digital: %d", digitalRead(pin.number));
                break;
            case HS_CONFIG_PIN_TYPE_READ_A:
                dbg("- analog: %d", analogRead(pin.number));
                break;
            case HS_CONFIG_PIN_TYPE_SWITCH:
                dbg("- switch: %d", digitalRead(pin.number));
                break;
            case HS_CONFIG_PIN_TYPE_TEMP:
                dbg("- temperature: %f˚C", hs_read_temperature(pin.number));
                break;
            case HS_CONFIG_PIN_TYPE_HUM:
                dbg("- humidity: %f", hs_read_humidity(pin.number));
                break;
        }
    }
}

float hs_read_temperature(uint8_t pin) {
    return 18.5;
}

float hs_read_humidity(uint8_t pin) {
    return 0;
}