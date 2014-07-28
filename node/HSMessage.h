/*
 * HSMessage.h
 *
 *  Created on: 19. 1. 2014
 *      Author: hejki
 */


#ifndef HSMESSAGE_H_
#define HSMESSAGE_H_

#include <stdio.h>
#include <stdlib.h>
#include "common.h"

#define HS_MESSAGE_TYPE_OK                  0
#define HS_MESSAGE_TYPE_ERROR               1

#define HS_MESSAGE_TYPE_PING                10
#define HS_MESSAGE_TYPE_GET_SRV_SETTING     11
#define HS_MESSAGE_TYPE_SET_SRV_SETTING     12
#define HS_MESSAGE_TYPE_GET_NODE_SETTING    13
#define HS_MESSAGE_TYPE_SET_NODE_SETTING    14
#define HS_MESSAGE_TYPE_GET_PIN_SETTING     15
#define HS_MESSAGE_TYPE_SET_PIN_SETTING     16

#define HS_MESSAGE_TYPE_DIGITAL_READ        20
#define HS_MESSAGE_TYPE_DIGITAL_WRITE       21
#define HS_MESSAGE_TYPE_ANALOG_READ         22
#define HS_MESSAGE_TYPE_ANALOG_WRITE        23

#define HS_MESSAGE_ERR_UNSUPPORTED_MSG      0
#define HS_MESSAGE_ERR_INVALID_CHECKSUM		1
#define HS_MESSAGE_ERR_NOT_IMPLEMENTED_MSG  2
#define HS_MESSAGE_ERR_MSG_INCORRECT_IMPL  	3
#define HS_MESSAGE_ERR_BAD_MSG_LENGTH      	4

class HSMessage {
protected:
	uint8_t id;
	uint8_t replyLength;
	char *replyBegin;
	char *replyPtr;

public:
	/**
	 * Create message implementation for specified type.
	 *
	 * @param type message type
	 * @param length length of message data
	 * @param msgId message identifier
	 * @param checksum message checksum
	 * @param data message data
	 */
	static HSMessage *createMessage(uint8_t type, uint8_t length, uint8_t msgId, uint8_t checksum, const char *data);

	virtual ~HSMessage();

	void process();

	const char *getReply() { return replyBegin; }
	uint8_t getReplySize() { return replyLength; }

private:
	void prepareReply(uint8_t type, uint8_t len);
	
protected:
	void prepareOkReply(uint8_t len);
	void createErrReply(uint8_t errorCode);

	void appendByte(uint8_t b);
	void append2Byte(uint16_t i);

	virtual void processInternal();
};


// HSErrorMessage (HS_MESSAGE_TYPE_ERROR)
class HSErrorMessage : public HSMessage {
	uint8_t errorCode;

public:
	HSErrorMessage(uint8_t errorCode) { this->errorCode = errorCode; }
	void processInternal();
};

// HSPingMessage (HS_MESSAGE_TYPE_PING)
class HSPingMessage : public HSMessage {
public:
	void processInternal();
};

// HSAnalogReadMessage (HS_MESSAGE_TYPE_ANALOG_READ)
class HSAnalogReadMessage : public HSMessage {
	uint8_t pin;

public:
	HSAnalogReadMessage(uint8_t pin) { this->pin = pin; }
	void processInternal();
};

#endif /* HSMESSAGE_H_ */
