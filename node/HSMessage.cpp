#include "HSMessage.h"

#define MSG_MIN_LENGTH              4
#define MSG_DATA_POS                3

static uint8_t calculateChecksum(uint8_t type, uint8_t length, uint8_t msgId, const char *data) {
	uint8_t checksum = (uint8_t) (type + length + msgId);

	for (int i = MSG_DATA_POS; i < length + MSG_DATA_POS; i++) {
		checksum += (uint8_t) data[i] & 0xFF;
	}
	return 256 - checksum;
}

HSMessage::~HSMessage(void) {
	if (replyBegin) delete[] replyBegin;
}

HSMessage *HSMessage::createMessage(uint8_t type, uint8_t length, uint8_t msgId, uint8_t checksum, const char *data) {
#ifndef NO_CRC_CHECK
	// check checksum
	if (checksum != calculateChecksum(type, length, msgId, data)) {
		LOG("Invalid crc=%x, correct=%x", checksum, calculateChecksum(type, length, msgId, data));
		return new HSErrorMessage(ERR_INVALID_CHECKSUM);
	}
#endif

	// create message
	HSMessage *msg;
	switch (type) {
	case HS_MESSAGE_TYPE_PING: // ping
		msg = new HSPingMessage();
		break;
	case HS_MESSAGE_TYPE_ANALOG_READ: // analog read
		if (length != 1) {
			return new HSErrorMessage(HS_MESSAGE_ERR_BAD_MSG_LENGTH);
		}
		msg = new HSAnalogReadMessage(data[0]);
		break;

	default:
		return new HSErrorMessage(HS_MESSAGE_ERR_UNSUPPORTED_MSG);
	}

	msg->id = msgId;
	return msg;
}

void HSMessage::process() {
	dbg("Process message");
	processInternal();

	if (!replyBegin || replyPtr != (replyBegin + replyLength -1)) {
		if (replyBegin) delete[] replyBegin;
		createErrReply(HS_MESSAGE_ERR_MSG_INCORRECT_IMPL);
	}

	*replyPtr = calculateChecksum(replyBegin[0],
			replyLength - MSG_MIN_LENGTH, id, replyBegin);
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

// HSErrorMessage (HS_MESSAGE_TYPE_ERROR)
void HSErrorMessage::processInternal() {
	createErrReply(errorCode);
}

// HSPingMessage (HS_MESSAGE_TYPE_PING)
void HSPingMessage::processInternal() {
    dbg("Process internal on PingMessage");
	prepareOkReply(0);
}

// HSAnalogReadMessage (HS_MESSAGE_TYPE_ANALOG_READ)
void HSAnalogReadMessage::processInternal() {
    dbg("Process internal on AnalogReadMessage");
	uint16_t value = analogRead(this->pin);

	prepareOkReply(2);
	append2Byte(value);
}