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

#define HS_MESSAGE_ERR_UNSUPPORTED_MSG      0
#define HS_MESSAGE_ERR_INVALID_CHECKSUM		1
#define HS_MESSAGE_ERR_NOT_IMPLEMENTED_MSG  2
#define HS_MESSAGE_ERR_MSG_INCORRECT_IMPL  	3
#define HS_MESSAGE_ERR_BAD_MSG_LENGTH      	4
#define HS_MESSAGE_ERR_NOT_SETTING_MODE    	5
#define HS_MESSAGE_ERR_IN_SETTING_MODE    	6

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

    void send(byte *ip);

	const char *getReply() { return replyBegin; }
	uint8_t getReplySize() { return replyLength; }
    
private:
    void process();
    static HSMessage *createMessage(uint8_t type, uint8_t length, const char *requestData);
	
protected:
    
    /**
     *  Prepare data message with header, message id and length.
     *
     *  @param type response type
     *  @param len  response data length
     */
    void prepareReply(uint8_t type, uint8_t len);
    
    /**
     *  Prepare data message with OK header, message id and
     *  length.
     *
     *  @param len message length
     */
	void prepareOkReply(uint8_t len);
    
    /**
     *  Prepara data message with error type.
     *
     *  @param errorCode specified error code
     */
	void createErrReply(uint8_t errorCode);

    /**
     *  Append byte to response message.
     *
     *  @param b byte to message
     */
	void appendByte(uint8_t b);
    
    /**
     *  Append two bytes to response message.
     *
     *  @param i two bytes to message
     */
	void append2Byte(uint16_t i);

	virtual void processInternal();
};

// MARK: Response messages

// HSErrorMessage (HS_MESSAGE_TYPE_ERROR)
class HSErrorMessage : public HSMessage {
	uint8_t errorCode;

public:
	HSErrorMessage(uint8_t errorCode) { this->errorCode = errorCode; }
	void processInternal();
};

// HSPinValueMessage (HS_MESSAGE_TYPE_PIN_VALUE)
class HSValueMessage : public HSMessage {
    uint8_t pinIndex;
    uint16_t value;
    
public:
    HSValueMessage(uint8_t pinIndex, uint16_t value) {
        this->pinIndex = pinIndex;
        this->value = value;
    }
    
    void processInternal();
};

// MARK: General messages

// HSPingMessage (HS_MESSAGE_TYPE_PING)
class HSPingMessage : public HSMessage {
public:
	void processInternal();
};

// HSGetLocalInfoMessage (HS_MESSAGE_TYPE_GET_LOCAL_INFO)
class HSGetLocalInfoMessage : public HSMessage {
    uint8_t responseCode;
    
public:
    HSGetLocalInfoMessage(uint8_t responseCode) { this->responseCode = responseCode; }
    void processInternal();
};

// MARK: Settings messages

// HSEnterSettingsModeMessage (HS_MESSAGE_TYPE_ENTER_SETTING_MODE)
class HSEnterSettingsModeMessage : public HSMessage {
public:
    void processInternal();
};

// HSGetNodeSettingsMessage (HS_MESSAGE_TYPE_GET_NODE_SETTING)
class HSGetNodeSettingsMessage : public HSMessage {
public:
    void processInternal();
};

// HSSetNodeSettingsMessage (HS_MESSAGE_TYPE_SET_NODE_SETTING)
class HSSetNodeSettingsMessage : public HSGetNodeSettingsMessage {
public:
    HSSetNodeSettingsMessage(const char *data);
};

// HSGetPinSettingsMessage (HS_MESSAGE_TYPE_GET_PIN_SETTING)
class HSGetPinSettingsMessage : public HSMessage {
protected:
    uint8_t pin;
    
public:
    HSGetPinSettingsMessage(uint8_t pin) { this->pin = pin; }
    void processInternal();
};

// HSSetPinSettingsMessage (HS_MESSAGE_TYPE_SET_PIN_SETTING)
class HSSetPinSettingsMessage : public HSGetPinSettingsMessage {
public:
    HSSetPinSettingsMessage(const char *data);
};

// HSClearPinSettingsMessage (HS_MESSAGE_TYPE_CLEAR_PIN_SETTING)
class HSClearPinSettingsMessage : public HSMessage {
public:
    void processInternal();
};

// HSCommitPinSettingsMessage (HS_MESSAGE_TYPE_COMMIT_SETTING)
class HSCommitSettingsMessage : public HSMessage {
public:
    void processInternal();
};

// MARK: Simple messages

// HSDigitalReadMessage (HS_MESSAGE_TYPE_DIGITAL_READ)
// HSDigitalWriteMessage (HS_MESSAGE_TYPE_DIGITAL_WRITE)

// HSAnalogReadMessage (HS_MESSAGE_TYPE_ANALOG_READ)
class HSAnalogReadMessage : public HSMessage {
    uint8_t pin;
    
public:
    HSAnalogReadMessage(uint8_t pin) { this->pin = pin; }
    void processInternal();
};

#endif /* HSMESSAGE_H_ */
