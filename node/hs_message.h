/*
 * HSMessage.h
 *
 *  Created on: 19. 1. 2014
 *      Author: hejki
 */


#ifndef __Node__HSMessage__
#define __Node__HSMessage__

#include <stdio.h>
#include <stdlib.h>
#include "common.h"

#define HS_MESSAGE_TYPE_OK                  0
#define HS_MESSAGE_TYPE_ERROR               1
#define HS_MESSAGE_TYPE_NODE_INFO           2
#define HS_MESSAGE_TYPE_PIN_VALUE           3

#define HS_MESSAGE_TYPE_PING                10
#define HS_MESSAGE_TYPE_GET_LOCAL_INFO      11

#define HS_MESSAGE_TYPE_ENTER_SETTING_MODE  20
#define HS_MESSAGE_TYPE_GET_NODE_SETTING    21
#define HS_MESSAGE_TYPE_SET_NODE_SETTING    22
#define HS_MESSAGE_TYPE_GET_PIN_SETTING     23
#define HS_MESSAGE_TYPE_SET_PIN_SETTING     24
#define HS_MESSAGE_TYPE_CLEAR_PIN_SETTING   25
#define HS_MESSAGE_TYPE_COMMIT_SETTING      26

#define HS_MESSAGE_TYPE_DIGITAL_READ        30
#define HS_MESSAGE_TYPE_DIGITAL_WRITE       31
#define HS_MESSAGE_TYPE_ANALOG_READ         32
#define HS_MESSAGE_TYPE_ANALOG_WRITE        33

// extended messages
#ifdef MEMORY_FREE
#define HS_MESSAGE_TYPE_FREE_MEMORY         100
#endif

#define HS_MESSAGE_ERR_UNSUPPORTED_MSG      0
#define HS_MESSAGE_ERR_INVALID_CHECKSUM		1
#define HS_MESSAGE_ERR_NOT_IMPLEMENTED_MSG  2
#define HS_MESSAGE_ERR_MSG_INCORRECT_IMPL  	3
#define HS_MESSAGE_ERR_BAD_MSG_LENGTH      	4
#define HS_MESSAGE_ERR_NOT_SETTING_MODE    	5
#define HS_MESSAGE_ERR_IN_SETTING_MODE    	6

struct HSMessage {
    bool prepared;
    uint8_t requestId;
    uint8_t replyLength;
    char *replyBegin;
    char *replyPtr;
};

/**
 *  Create message instance.
 *
 *  @param messageId message identifier
 */
HSMessage *hs_message_create(uint8_t messageId);

/**
 *  Prepare data message with error type.
 *
 *  @param errorCode specified error code
 */
void hs_message_fill_error(HSMessage *msg, uint8_t errorCode);

/**
 *  Process request message and send response.
 *
 *  @param msg      message to process
 *  @param type     message type
 *  @param length   length of message data
 *  @param checksum message checksum
 *  @param data     message data
 */
void hs_message_process(HSMessage *msg, uint8_t type, uint8_t length, uint8_t checksum, const char *data);

/**
 *  Send message.
 *
 *  @param msg message reference
 *  @param ip  ip of server or NULL to send server from configuration
 */
void hs_message_send(HSMessage *msg, byte *ip);

/**
 *  Send pin value to server.
 *
 *  @param pinIndex index of pin configuration
 *  @param value    value of pin
 */
void hs_message_send_pin_value(uint8_t pinIndex, uint16_t value);

/**
 *  Release message memory.
 *
 *  @param msg message reference
 */
void hs_message_release(HSMessage **msg);

#endif /* __Node__HSMessage__ */
