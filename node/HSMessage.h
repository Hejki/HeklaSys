/*
 * HSMessage.h
 *
 *  Created on: 19. 1. 2014
 *      Author: hejki
 */


#ifndef HSMESSAGE_H_
#define HSMESSAGE_H_

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define MSG_MIN_LENGTH 			4
#define MSG_DATA_POS			3

#define ERR_UNSUPPORTED_MSG		0
#define ERR_INVALID_CHECKSUM	1
#define ERR_NOT_IMPLEMENTED_MSG	2
#define ERR_MSG_INCORRECT_IMPL  3
#define ERR_BAD_MSG_LENGTH      4

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
	const uint8_t getReplySize() { return replyLength; }

protected:
	void prepareReply(uint8_t type, uint8_t len);
	void prepareOkReply(uint8_t len);
	void createErrReply(uint8_t errorCode);

	void appendByte(uint8_t b);
	void append2Byte(uint16_t i);

	virtual void processInternal();
};


#pragma mark - HSErrorMessage (1)
class HSErrorMessage : public HSMessage {
	uint8_t errorCode;

public:
	HSErrorMessage(uint8_t errorCode) { this->errorCode = errorCode; }
	void processInternal();
};

#pragma mark - HSPingMessage (10)
class HSPingMessage : public HSMessage {
public:
	void processInternal();
};

#pragma mark - HSAnalogReadMessage (22)
class HSAnalogReadMessage : public HSMessage {
	uint8_t pin;

public:
	HSAnalogReadMessage(uint8_t pin) { this->pin = pin; }
	void processInternal();
};

#endif /* HSMESSAGE_H_ */
