/*
 * HeklaSys.h
 *
 *  Created on: 19. 1. 2014
 *      Author: hejki
 */

#ifndef HEKLASYS_H_
#define HEKLASYS_H_

#include <Arduino.h>
#include "HSMessage.h"

#define DEBUG 1
#define ETHERNET_BUF_SIZE 512

class HeklaSys {
	static uint16_t udpLocalPort;
	static uint16_t udpRemotePort;
	static uint8_t udpRemoteIp[4];

public:
	bool setup();
	void loop();

private:
	static void udpReceive(word port, byte ip[4], const char *data, word len);

#if defined(DEBUG)
	/**
	 * Log message to serial. For logging use macro LOG
	 * not this method. Method is implemented in HeklaSys_log.cpp
	 *
	 * 'b' boolean
	 * 's' string
	 * 'c' character
	 * 'd' decimal integer
	 * 'B' binary integer
	 * 'l' decimal long
	 * 'o' octal integer
	 * 'x' hexadecimal integer
	 * 'f' floating point decimal number
	 * 't' date/time
	 * '%' percent character
	 * Place % at end of string to supress new line.
	 */
	static void log(const char *file, int line, const char *msg, ...);
#endif //DEBUG

};

#if defined(DEBUG)
#define LOG(msg, ...) HeklaSys::log(__FILE__, __LINE__, msg, ##__VA_ARGS__)
#else
#define LOG(msg, ...) /* message: msg */
#endif //DEBUG


#endif /* HEKLASYS_H_ */
