/*
 * HeklaSys.cpp
 *
 *  Created on: 19. 1. 2014
 *      Author: hejki
 */

#include "HeklaSys.h"
#include <Time.h>
#include <EtherCard.h>
#include <MCP79412RTC.h>

#define NO_RTC 1

byte Ethernet::buffer[ETHERNET_BUF_SIZE];
uint16_t HeklaSys::udpLocalPort = 12321;
uint16_t HeklaSys::udpRemotePort = 32123;
uint8_t HeklaSys::udpRemoteIp[4] = {10, 0, 0, 7};

#if NO_RTC
static byte macAddress[] = { 0x74, 0x69, 0x69, 0x2D, 0x30, 0x31 };
#endif

bool HeklaSys::setup() {
#if defined(DEBUG)
	Serial.begin(115200);
#endif

#if NO_RTC
	if (ether.begin(sizeof Ethernet::buffer, macAddress) == 0) {
		LOG("Cannot init ether card with static MAC address.");
		return false;
	}
#else
	// RTC
	if (!RTC.isRunning()) {
		LOG("RTC was not running set default time");
		setTime(0, 0, 0, 1, 1, 0); // 1.1.2000 00:00:00
		RTC.set(now());
		RTC.vbaten(true);
	}

	setSyncProvider(RTC.get);
	if (timeStatus() != timeSet) {
		LOG("Unable to sync with the RTC");
		return false;
	}

	// MAC address and Ethernet setup
	uint8_t rtcId[8];
	RTC.idRead(rtcId);
	if (ether.begin(sizeof Ethernet::buffer, rtcId) == 0) {
		LOG("Cannot init ether card with MAC: %x:%x:%x:%x:%x:%x",
				rtcId[0], rtcId[1], rtcId[2], rtcId[3], rtcId[4], rtcId[5], rtcId[6]);
		return false;
	}
#endif

	if (!ether.dhcpSetup()) {
		LOG("DHCP failed.");
	} else {
		LOG("IP: %d.%d.%d.%d", ether.myip[0], ether.myip[1], ether.myip[2], ether.myip[3]);
	}

	ether.udpServerListenOnPort(&HeklaSys::udpReceive, HeklaSys::udpLocalPort);

	return true;
}

void HeklaSys::loop() {
//	HSMessage *msg = receiveMessage();

//	msg->process();

	// send response
//	ether.sendUdp(msg->getReply(), msg->getReplySize(),
//			udpLocalPort, udpRemoteIp, udpRemotePort);

//	delete msg;

	ether.packetLoop(ether.packetReceive());
}

void HeklaSys::udpReceive(word port, byte ip[4], const char *data, word messageLength) {
	LOG("%d.%d.%d.%d:%d, len: %d", ip[0], ip[1], ip[2], ip[3], port, messageLength);

	if (messageLength >= 3) {
		uint8_t type = data[0];
		uint8_t length = data[1];
		uint8_t id = data[2];

		if (messageLength != (length + 4)) {
			// error message bad length
			LOG("Bad message length, expect (%d + 4) but was %d", length, messageLength);
			return;
		}

		uint8_t crc = data[messageLength - 1];

		// Info
		LOG("t: %d, l: %d, id: %d, crc: %d", type, length, id, crc);

		HSMessage *message = HSMessage::createMessage(type, length, id, crc, data);
		LOG("msg: %d", message);

		message->process();
		ether.sendUdp(message->getReply(), message->getReplySize(),
				HeklaSys::udpLocalPort, HeklaSys::udpRemoteIp, HeklaSys::udpRemotePort);

		delete message;
	}
}

#if defined(DEBUG)
static void printNum(int num) {
	if (num < 10) Serial.print('0');
	Serial.print(num);
}

void HeklaSys::log(const char *file, int line, const char *msg, ...) {
// print date and time
	Serial.print(year());
	Serial.print("-");
	printNum(month());
	Serial.print("-");
	printNum(day());

	Serial.print(" ");
	printNum(hour());
	Serial.print(":");
	printNum(minute());
	Serial.print(":");
	printNum(second());
	Serial.print(" ");

	Serial.print(strrchr(file, '/'));
	Serial.print(":");
	Serial.print(line);
	Serial.print(" ");

	va_list args;
	va_start(args, msg);

	for (; *msg != 0; ++msg) {
		if (*msg == '%') {
			++msg;

			switch (*msg) {
				case '\0':
				return;
				case '%':
				Serial.print(*msg);
				break;
				case 'b':
				Serial.print(va_arg(args, int) == 0 ? "false" : "true");
				break;
				case 's':
				Serial.print((char *)va_arg(args, int));
				break;
				case 'c':
				Serial.print(va_arg(args, int));
				break;
				case 'd':
				Serial.print(va_arg(args, int), DEC);
				break;
				case 'B':
				Serial.print(va_arg(args, int), BIN);
				break;
				case 'l':
				Serial.print(va_arg(args, long), DEC);
				break;
				case 'o':
				Serial.print(va_arg(args, int), OCT);
				break;
				case 'x':
				Serial.print(va_arg(args, int), HEX);
				break;
				case 'f':
				Serial.print(va_arg(args, double), DEC);
				break;
			}
		} else {
			Serial.print(*msg);
		}
	}
	Serial.print("\r\n");
}
#endif //DEBUG
