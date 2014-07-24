#include "HSMessage.h"
#include <Arduino.h>

static uint8_t calculateChecksum(uint8_t type, uint8_t length, uint8_t msgId, const char *data) {
	uint8_t checksum = (uint8_t) (type + length + msgId);

	for (int i = MSG_DATA_POS; i < length + MSG_DATA_POS; i++) {
		Serial.println(data[i] & 0xFF);
		checksum += (uint8_t) data[i] & 0xFF;
	}
	return 256 - checksum;
}

HSMessage::~HSMessage(void) {
	if (replyBegin) delete[] replyBegin;
}

HSMessage *HSMessage::createMessage(uint8_t type, uint8_t length, uint8_t msgId, uint8_t checksum, const char *data) {
	// check checksum
	if (checksum != calculateChecksum(type, length, msgId, data)) {
		return new HSErrorMessage(ERR_INVALID_CHECKSUM);
	}

	// create message
	HSMessage *msg;
	switch (type) {
	case 10: // ping
		msg = new HSPingMessage();
		break;
	case 22: // analog read
		if (length != 1) {
			return new HSErrorMessage(ERR_BAD_MSG_LENGTH);
		}
		msg = new HSAnalogReadMessage(data[0]);
		break;

	default:
		return new HSErrorMessage(ERR_UNSUPPORTED_MSG);
	}

	msg->id = msgId;
	return msg;
}

void HSMessage::process() {
	Serial.println("Process message");
	processInternal();

	if (!replyBegin || replyPtr != (replyBegin + replyLength -1)) {
		if (replyBegin) delete[] replyBegin;
		createErrReply(ERR_MSG_INCORRECT_IMPL);
	}

	*replyPtr = calculateChecksum(replyBegin[0],
			replyLength - MSG_MIN_LENGTH, id, replyBegin);
}

void HSMessage::processInternal() {
	createErrReply(ERR_NOT_IMPLEMENTED_MSG);
}

void HSMessage::prepareReply(uint8_t type, uint8_t len) {
	replyLength = len + MSG_MIN_LENGTH;
	replyBegin = new char[replyLength];
	replyBegin[0] = 0;
	replyBegin[1] = len;
	replyBegin[2] = id;
	replyPtr = replyBegin + 3;
}

void HSMessage::prepareOkReply(uint8_t len) {
	prepareReply(0, len);
}

void HSMessage::createErrReply(uint8_t errorCode) {
	Serial.print("Create err reply, code: ");
	Serial.println(errorCode);

	prepareReply(1, 1);
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

#pragma mark - HSErrorMessage (1)

void HSErrorMessage::processInternal() {
	createErrReply(errorCode);
}

#pragma mark - HSPingMessage (10)

void HSPingMessage::processInternal() {
	Serial.println("Process internal on PingMessage");
	prepareOkReply(0);
}

#pragma mark - HSAnalogReadMessage (22)

void HSAnalogReadMessage::processInternal() {
	Serial.println("Process internal on AnalogReadMessage");
	uint16_t value = analogRead(this->pin);

	prepareOkReply(2);
	append2Byte(value);
}
