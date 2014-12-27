#include <EtherCard.h>

#include "HSMessage.h"
#include "hs_config.h"

#define MSG_MIN_LENGTH              4
#define MSG_DATA_POS                3

static uint8_t calculateChecksum(uint8_t type, uint8_t length, uint8_t msgId, const char *data) {
    uint8_t checksum = (uint8_t) (type + length + msgId);
    
    for (int i = MSG_DATA_POS; i < length + MSG_DATA_POS; i++) {
        checksum += (uint8_t) data[i] & 0xFF;
    }
    return 256 - checksum;
}

HSMessage *HSMessage::createMessage(uint8_t type, uint8_t length, const char *requestData) {
    
    switch (type) {
        case HS_MESSAGE_TYPE_PING: // ping
            return new HSPingMessage();
            
        case HS_MESSAGE_TYPE_GET_LOCAL_INFO:
            return new HSGetLocalInfoMessage(HS_MESSAGE_TYPE_OK);
            
        case HS_MESSAGE_TYPE_ENTER_SETTING_MODE:
            return new HSEnterSettingsModeMessage();
            
        case HS_MESSAGE_TYPE_GET_NODE_SETTING:
            return new HSGetNodeSettingsMessage();
            
        case HS_MESSAGE_TYPE_SET_NODE_SETTING:
            if (!status.settingsMode) {
                return new HSErrorMessage(HS_MESSAGE_ERR_NOT_SETTING_MODE);
            }
            if (length != 9) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSSetNodeSettingsMessage(requestData);
            
        case HS_MESSAGE_TYPE_GET_PIN_SETTING:
            if (requestData[0] < 0 || requestData[0] > HS_CONFIG_MAX_PIN_INDEX) {
                return new HSErrorMessage(HS_MESSAGE_ERR_MSG_INCORRECT_IMPL);
            }
            return new HSGetPinSettingsMessage(requestData[0]);
            
        case HS_MESSAGE_TYPE_SET_PIN_SETTING:
            if (!status.settingsMode) {
                return new HSErrorMessage(HS_MESSAGE_ERR_NOT_SETTING_MODE);
            }
            if (length != 4) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSSetPinSettingsMessage(requestData);
            
        case HS_MESSAGE_TYPE_CLEAR_PIN_SETTING:
            if (!status.settingsMode) {
                return new HSErrorMessage(HS_MESSAGE_ERR_NOT_SETTING_MODE);
            }
            return new HSClearPinSettingsMessage();
            
        case HS_MESSAGE_TYPE_COMMIT_SETTING:
            if (!status.settingsMode) {
                return new HSErrorMessage(HS_MESSAGE_ERR_NOT_SETTING_MODE);
            }
            return new HSCommitSettingsMessage();
            
            /* Simple Task */
        case HS_MESSAGE_TYPE_DIGITAL_READ:
            if (length != 1) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSErrorMessage(HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            
        case HS_MESSAGE_TYPE_DIGITAL_WRITE:
            if (length != 2) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSErrorMessage(HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            
        case HS_MESSAGE_TYPE_ANALOG_READ: // analog read
            if (length != 1) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSAnalogReadMessage(requestData[0]);
            
        case HS_MESSAGE_TYPE_ANALOG_WRITE:
            if (length != 3) {
                return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            }
            return new HSErrorMessage(HS_MESSAGE_ERR_UNSUPPORTED_MSG);
    }
    return new HSErrorMessage(HS_MESSAGE_ERR_UNSUPPORTED_MSG);
}

HSMessage::~HSMessage(void) {
    if (replyBegin) delete[] replyBegin;
}

HSMessage *HSMessage::createMessage(uint8_t type, uint8_t length, uint8_t msgId, uint8_t checksum, const char *data) {
    
    
    
#ifndef NO_CRC_CHECK
    // check checksum
    if (checksum != calculateChecksum(type, length, msgId, data)) {
        LOG("Invalid crc=%x, correct=%x", checksum, calculateChecksum(type, length, msgId, data));
        
        HSMessage *msg = new HSErrorMessage(ERR_INVALID_CHECKSUM);
        msg->id = msgId;
        return msg;
    }
#endif
    
    HSMessage *msg = createMessage(type, length, data + 3);
    msg->id = msgId;
    return msg;
}

void HSMessage::process() {
    //	dbg("Process message");
    processInternal();
    
    if (!replyBegin || replyPtr != (replyBegin + replyLength -1)) {
        if (replyBegin) delete[] replyBegin;
        createErrReply(HS_MESSAGE_ERR_MSG_INCORRECT_IMPL);
    }
    
    *replyPtr = calculateChecksum(replyBegin[0],
                                  replyLength - MSG_MIN_LENGTH, id, replyBegin);
}

void HSMessage::send(byte *ip) {
    process();
    
    if (ip != NULL) {
        uint16_t port = hs_node_config.serverPort;
        
        if (port == 0 || port == 0xFFFF) port = HS_DEF_UDP_SERVER_PORT;
        dbg("rs: %d, lp: %d, s: %d.%d.%d.%d:%d", getReplySize(), udpLocalPort, ip[0], ip[1], ip[2], ip[3], port);
        
        ether.sendUdp(getReply(), getReplySize(), HS_DEF_UDP_SERVER_PORT, ip, port);
    } else {
        dbg("nrs: %d, lp: %d, s: %d.%d.%d.%d:%d", getReplySize(), udpLocalPort, hs_node_config.serverIp[0], hs_node_config.serverIp[1], hs_node_config.serverIp[2], hs_node_config.serverIp[3], hs_node_config.serverPort);
        
        ether.sendUdp(getReply(), getReplySize(), HS_DEF_UDP_SERVER_PORT, hs_node_config.serverIp, hs_node_config.serverPort);
    }
}

void HSMessage::processInternal() {
    createErrReply(HS_MESSAGE_ERR_NOT_IMPLEMENTED_MSG);
}

void HSMessage::prepareReply(uint8_t type, uint8_t len) {
    replyLength = len + MSG_MIN_LENGTH;
    replyBegin = new char[replyLength];
    replyBegin[0] = type;
    replyBegin[1] = len;
    replyBegin[2] = id;
    replyPtr = replyBegin + 3;
}

void HSMessage::prepareOkReply(uint8_t len) {
    prepareReply(HS_MESSAGE_TYPE_OK, len);
}

void HSMessage::createErrReply(uint8_t errorCode) {
    dbg("Create err reply, code: %x", errorCode);
    
    prepareReply(HS_MESSAGE_TYPE_ERROR, 1);
    *replyPtr = errorCode;
    replyPtr++;
}

void HSMessage::appendByte(uint8_t b) {
    *replyPtr++ = b;
}

void HSMessage::append2Byte(uint16_t i) {
    *replyPtr++ = (i & 0xFF);
    *replyPtr++ = ((i >> 8) & 0xFF);
}

// MARK: Response messages

// HSErrorMessage (HS_MESSAGE_TYPE_ERROR)
void HSErrorMessage::processInternal() {
    createErrReply(errorCode);
}

// HSPinValueMessage (HS_MESSAGE_TYPE_PIN_VALUE)
void HSValueMessage::processInternal() {
    prepareReply(HS_MESSAGE_TYPE_PIN_VALUE, 3);
    appendByte(this->pinIndex);
    append2Byte(this->value);
}

// MARK: General messages

// HSPingMessage (HS_MESSAGE_TYPE_PING)
void HSPingMessage::processInternal() {
    prepareOkReply(0);
}

// HSGetLocalInfoMessage (HS_MESSAGE_TYPE_GET_LOCAL_INFO)
void HSGetLocalInfoMessage::processInternal() {
    prepareReply(this->responseCode, 6);
    appendByte(ether.myip[0]);
    appendByte(ether.myip[1]);
    appendByte(ether.myip[2]);
    appendByte(ether.myip[3]);
    append2Byte(HS_DEF_UDP_SERVER_PORT);
}

// MARK: Settings messages

// HSEnterSettingsModeMessage (HS_MESSAGE_TYPE_ENTER_SETTING_MODE)
void HSEnterSettingsModeMessage::processInternal() {
    hs_config_enter_settings_mode();
    prepareOkReply(0);
}

// HSGetNodeSettingsMessage (HS_MESSAGE_TYPE_GET_NODE_SETTING)
void HSGetNodeSettingsMessage::processInternal() {
    prepareOkReply(9);
    appendByte(hs_node_config.statusSendInterval);
    appendByte(hs_node_config.statusSendTimeUnit);
    appendByte(hs_node_config.numberOfPinPositions);
    appendByte(hs_node_config.serverIp[0]);
    appendByte(hs_node_config.serverIp[1]);
    appendByte(hs_node_config.serverIp[2]);
    appendByte(hs_node_config.serverIp[3]);
    append2Byte(hs_node_config.serverPort);
}

// HSSetNodeSettingsMessage (HS_MESSAGE_TYPE_SET_NODE_SETTING)
HSSetNodeSettingsMessage::HSSetNodeSettingsMessage(const char *data) {
    uint8_t i = 0;
    
    hs_node_config.statusSendInterval = data[i++] & 0xFF;
    hs_node_config.statusSendTimeUnit = data[i++] & 0xFF;
    hs_node_config.numberOfPinPositions = data[i++] & 0xFF;
    hs_node_config.serverIp[0] = data[i++] & 0xFF;
    hs_node_config.serverIp[1] = data[i++] & 0xFF;
    hs_node_config.serverIp[2] = data[i++] & 0xFF;
    hs_node_config.serverIp[3] = data[i++] & 0xFF;
    hs_node_config.serverPort = data[i++] & 0xFF;
    hs_node_config.serverPort |= data[i++] << 8;
}

// HSGetPinSettingsMessage (HS_MESSAGE_TYPE_GET_PIN_SETTING)
void HSGetPinSettingsMessage::processInternal() {
    HSPinConf pinConfig = hs_pin_configurations[this->pin];
    
    prepareOkReply(3);
    appendByte(pinConfig.number);
    appendByte(pinConfig.type);
    appendByte(pinConfig.config);
}

// HSSetPinSettingsMessage (HS_MESSAGE_TYPE_SET_PIN_SETTING)
HSSetPinSettingsMessage::HSSetPinSettingsMessage(const char *data) : HSGetPinSettingsMessage(data[0]) {
    HSPinConf *pinConfig = &(hs_pin_configurations[this->pin]);
    
    pinConfig->number = data[1];
    pinConfig->type = data[2];
    pinConfig->config = data[3];
}

// HSClearPinSettingsMessage (HS_MESSAGE_TYPE_CLEAR_PIN_SETTING)
void HSClearPinSettingsMessage::processInternal() {
    HSPinConf *pin;
    
    for (uint8_t i = 0; i <= HS_CONFIG_MAX_PIN_INDEX; i++) {
        pin = &(hs_pin_configurations[i]);
        pin->number = 0;
        pin->type = HS_CONFIG_PIN_TYPE_NONE;
        pin->config = 0;
    }
    
    prepareOkReply(0);
}

// HSCommitSettingsMessage (HS_MESSAGE_TYPE_COMMIT_SETTING)
void HSCommitSettingsMessage::processInternal() {
    hs_config_save();
    prepareOkReply(0);
}

// MARK: Simple messages

// HSDigitalReadMessage (HS_MESSAGE_TYPE_DIGITAL_READ)
// HSDigitalWriteMessage (HS_MESSAGE_TYPE_DIGITAL_WRITE)

// HSAnalogReadMessage (HS_MESSAGE_TYPE_ANALOG_READ)
void HSAnalogReadMessage::processInternal() {
    uint16_t value = analogRead(this->pin);
    
    prepareOkReply(2);
    append2Byte(value);
}

// HSAnalogWriteMessage (HS_MESSAGE_TYPE_ANALOG_WRITE)