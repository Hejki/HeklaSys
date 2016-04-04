#include <EtherCard.h>

#include "hs_message.h"
#include "hs_config.h"

#define MSG_MIN_LENGTH              4
#define MSG_DATA_POS                3

/**
 *  Calculate checksum for message data.
 *
 *  @param type   type of message
 *  @param length message data length
 *  @param msgId  message id
 *  @param data   message data pointer
 *
 *  @return checksum for message
 */
static uint8_t hs_message_calculate_checksum(uint8_t type, uint8_t length, uint8_t msgId, const char *data) {
    uint8_t checksum = (uint8_t) (type + length + msgId);
    
    for (int i = MSG_DATA_POS; i < length + MSG_DATA_POS; i++) {
        checksum += (uint8_t) data[i] & 0xFF;
    }
    return 256 - checksum;
}

/**
 *  Prepare data message with header, message id and length.
 *
 *  @param type response type
 *  @param len  response data length
 */
static void hs_message_prepare_reply(HSMessage *msg, uint8_t type, uint8_t len) {
    msg->replyLength = len + MSG_MIN_LENGTH;
    msg->replyBegin = new char[msg->replyLength];
    msg->replyBegin[0] = type;
    msg->replyBegin[1] = len;
    msg->replyBegin[2] = msg->requestId;
    msg->replyPtr = msg->replyBegin + 3;
    msg->prepared = true;
}

/**
 *  Prepare data message with OK header, message id and
 *  length.
 *
 *  @param len message length
 */
static void hs_message_fill_ok_reply(HSMessage *msg, uint8_t len) {
    hs_message_prepare_reply(msg, HS_MESSAGE_TYPE_OK, len);
}

/**
 *  Append byte to response message.
 *
 *  @param b byte to message
 */
static void hs_message_append_byte(HSMessage *msg, uint8_t b) {
    *msg->replyPtr++ = b;
}

/**
 *  Append two bytes to response message.
 *
 *  @param i two bytes to message
 */
static void hs_message_append_2bytes(HSMessage *msg, uint16_t i) {
    *msg->replyPtr++ = (i & 0xFF);
    *msg->replyPtr++ = ((i >> 8) & 0xFF);
}

/**
 *  Process request and create response.
 *
 *  @param msg         response msg ref
 *  @param type        request type
 *  @param length      request length
 *  @param requestData request msg data
 */
static void hs_message_fill_response(HSMessage *msg, uint8_t type, uint8_t length, const char *requestData) {
    switch (type) {
        case HS_MESSAGE_TYPE_PING:
            hs_message_fill_ok_reply(msg, 0);
            break;
            
        case HS_MESSAGE_TYPE_GET_LOCAL_INFO:
            hs_message_prepare_reply(msg, HS_MESSAGE_TYPE_OK, 6);
            hs_message_append_byte(msg, ether.myip[0]);
            hs_message_append_byte(msg, ether.myip[1]);
            hs_message_append_byte(msg, ether.myip[2]);
            hs_message_append_byte(msg, ether.myip[3]);
            hs_message_append_2bytes(msg, HS_DEF_UDP_LOCAL_PORT);
            break;
            
        case HS_MESSAGE_TYPE_ENTER_SETTING_MODE:
            hs_config_enter_settings_mode();
            hs_message_fill_ok_reply(msg, 0);
            break;
            
        case HS_MESSAGE_TYPE_SET_NODE_SETTING:
            if (!status.settingsMode) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_NOT_SETTING_MODE);
                break;
            }
            if (length != 9) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
                break;
            }
            
            hs_node_config.statusSendInterval = requestData[0] & 0xFF;
            hs_node_config.statusSendTimeUnit = requestData[1] & 0xFF;
            hs_node_config.numberOfPinPositions = requestData[2] & 0xFF;
            hs_node_config.serverIp[0] = requestData[3] & 0xFF;
            hs_node_config.serverIp[1] = requestData[4] & 0xFF;
            hs_node_config.serverIp[2] = requestData[5] & 0xFF;
            hs_node_config.serverIp[3] = requestData[6] & 0xFF;
            hs_node_config.serverPort = requestData[7] & 0xFF;
            hs_node_config.serverPort |= requestData[8] << 8;
            // no break
        case HS_MESSAGE_TYPE_GET_NODE_SETTING:
            hs_message_fill_ok_reply(msg, 9);
            hs_message_append_byte(msg, hs_node_config.statusSendInterval);
            hs_message_append_byte(msg, hs_node_config.statusSendTimeUnit);
            hs_message_append_byte(msg, hs_node_config.numberOfPinPositions);
            hs_message_append_byte(msg, hs_node_config.serverIp[0]);
            hs_message_append_byte(msg, hs_node_config.serverIp[1]);
            hs_message_append_byte(msg, hs_node_config.serverIp[2]);
            hs_message_append_byte(msg, hs_node_config.serverIp[3]);
            hs_message_append_2bytes(msg, hs_node_config.serverPort);
            break;
            
        case HS_MESSAGE_TYPE_SET_PIN_SETTING:
            if (!status.settingsMode) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_NOT_SETTING_MODE);
                break;
            }
            if (length != 4) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
                break;
            } else {
                HSPinConf *pinConfig = hs_config_get_pin(requestData[0]);
                
                pinConfig->number = requestData[1];
                pinConfig->type = requestData[2];
                pinConfig->config = requestData[3];
                
                if (requestData[0] +1 > hs_node_config.numberOfPinPositions) {
                    hs_node_config.numberOfPinPositions = requestData[0] +1;
                }
                // no break
            }
            
        case HS_MESSAGE_TYPE_GET_PIN_SETTING:
            if (requestData[0] < 0 || requestData[0] > HS_CONFIG_MAX_PIN_INDEX) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_REQUEST_DATA);
            } else {
                HSPinConf pinConfig = *hs_config_get_pin(requestData[0]);
                
                hs_message_fill_ok_reply(msg, 3);
                hs_message_append_byte(msg, pinConfig.number);
                hs_message_append_byte(msg, pinConfig.type);
                hs_message_append_byte(msg, pinConfig.config);
            }
            break;
            
        case HS_MESSAGE_TYPE_CLEAR_PIN_SETTING:
            if (!status.settingsMode) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_NOT_SETTING_MODE);
            } else {
                HSPinConf *pin;
                
                for (uint8_t i = 0; i <= HS_CONFIG_MAX_PIN_INDEX; i++) {
                    pin = hs_config_get_pin(i);
                    pin->number = 0;
                    pin->type = HS_CONFIG_PIN_TYPE_NONE;
                    pin->config = 0;
                }
                
                hs_node_config.numberOfPinPositions = 0;
                hs_message_fill_ok_reply(msg, 0);
            }
            break;
            
        case HS_MESSAGE_TYPE_COMMIT_SETTING:
            if (!status.settingsMode) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_NOT_SETTING_MODE);
            } else {
                hs_config_save();
                hs_message_fill_ok_reply(msg, 0);
            }
            break;
            
            /* Simple Task */
        case HS_MESSAGE_TYPE_DIGITAL_READ:
            if (length != 1) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            } else {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            }
            break;
            
        case HS_MESSAGE_TYPE_DIGITAL_WRITE:
            if (length != 2) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            } else {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            }
            break;
            
        case HS_MESSAGE_TYPE_ANALOG_READ: // analog read
            if (length != 1) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            } else {
                hs_message_fill_ok_reply(msg, 2);
                hs_message_append_2bytes(msg, analogRead(requestData[0]));
            }
            break;
            
        case HS_MESSAGE_TYPE_ANALOG_WRITE:
            if (length != 3) {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_BAD_MSG_LENGTH);
            } else {
                hs_message_fill_error(msg, HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            }
            break;
            
#ifdef MEMORY_FREE
        case HS_MESSAGE_TYPE_FREE_MEMORY:
            hs_message_fill_ok_reply(msg, 1);
            hs_message_append_byte(msg, freeMemory());
            break;
#endif // MEMORY_FREE
            
        default:
            hs_message_fill_error(msg, HS_MESSAGE_ERR_UNSUPPORTED_MSG);
            break;
    }
}

/**
 *  Finish data in response msg. Check msg pointer and lenght, then add crc.
 *
 *  @param msg message reference
 */
static void hs_message_finish(HSMessage *msg) {
    if (!msg->replyBegin || msg->replyPtr != (msg->replyBegin + msg->replyLength -1)) {
        if (msg->replyBegin) {
            delete[] msg->replyBegin;
        }
        hs_message_fill_error(msg, HS_MESSAGE_ERR_MSG_INCORRECT_IMPL);
    }
    
    *msg->replyPtr = hs_message_calculate_checksum(msg->replyBegin[0], msg->replyLength - MSG_MIN_LENGTH, msg->requestId, msg->replyBegin);
}

// MARK: Public methods

HSMessage *hs_message_create(uint8_t messageId) {
    HSMessage *msg = new HSMessage();
    
    msg->prepared = false;
    msg->requestId = messageId;
    msg->replyLength = 0;
    msg->replyBegin = NULL;
    msg->replyPtr = NULL;
    return msg;
}

void hs_message_fill_error(HSMessage *msg, uint8_t errorCode) {
    hs_message_prepare_reply(msg, HS_MESSAGE_TYPE_ERROR, 1);
    hs_message_append_byte(msg, errorCode);
}


void hs_message_process(HSMessage *msg, uint8_t type, uint8_t length, uint8_t checksum, const char *data) {
#ifndef NO_CRC_CHECK
    // check checksum
    if (checksum != hs_message_calculate_checksum(type, length, msg->requestId, data)) {
        dbg("Invalid crc=%x, correct=%x", checksum, calculateChecksum(type, length, msgId, data));
        
        hs_message_fill_error(msg, ERR_INVALID_CHECKSUM);
    }
#endif
    
    if (!msg->prepared) {
        hs_message_fill_response(msg, type, length, data + MSG_DATA_POS);
    }
}

void hs_message_send(HSMessage *msg, byte *ip) {
    if (!msg->prepared) return;
    
    hs_message_finish(msg);
    
    if (ip != NULL) {
        uint16_t dport = hs_node_config.serverPort;
        
        if (dport == 0 || dport == 0xFFFF) dport = HS_DEF_UDP_SERVER_PORT;
        dbg("rs: %d, lp: %d, s: %d.%d.%d.%d:%d", msg->replyLength, HS_DEF_UDP_LOCAL_PORT, ip[0], ip[1], ip[2], ip[3], dport);
        
        ether.sendUdp(msg->replyBegin, msg->replyLength, HS_DEF_UDP_LOCAL_PORT, ip, dport);
    } else {
//        dbg("nrs: %d, lp: %d, s: %d.%d.%d.%d:%d", msg->replyLength, HS_DEF_UDP_LOCAL_PORT, hs_node_config.serverIp[0], hs_node_config.serverIp[1], hs_node_config.serverIp[2], hs_node_config.serverIp[3], hs_node_config.serverPort);
        
        ether.sendUdp(msg->replyBegin, msg->replyLength, HS_DEF_UDP_LOCAL_PORT, hs_node_config.serverIp, hs_node_config.serverPort);
    }
}

void hs_message_send_pin_value(uint8_t pinIndex, uint16_t value) {
    HSMessage *msg = hs_message_create(0);
    
    hs_message_prepare_reply(msg, HS_MESSAGE_TYPE_PIN_VALUE, 3);
    hs_message_append_byte(msg, pinIndex);
    hs_message_append_2bytes(msg, value);

    hs_message_send(msg, NULL);
    hs_message_release(&msg);
}

void hs_message_release(HSMessage **msg) {
    if ((*msg)->replyBegin) {
        delete[] (*msg)->replyBegin;
    }
    
    delete *msg;
    *msg = NULL;
}
