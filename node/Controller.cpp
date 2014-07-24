///*
// * Controller.cpp
// *
// *  Created on: 19. 1. 2014
// *      Author: hejki
// */
//
//#include "HeklaSys.h"
//#include "Controller.h"
//
//void Controller::process(const char *request) {
//	header(HTTP_STATUS_NOT_FOUND);
//	flush();
//}
//
//void Controller::header(int status) {
//	LOG("Send ACK");
//	ether.httpServerReplyAck();
//
//	LOG("Emit header with status %d", status);
//	write(PSTR(
//		"HTTP/1.1 %d\r\n"
//		"Content-Type: text/html\r\n"
//		"\r\n"
//	), status);
//}
//
//static char* wtoa (word value, char* ptr) {
//  if (value > 9) {
//	  ptr = wtoa(value / 10, ptr);
//  }
//  *ptr = '0' + value % 10;
//  *++ptr = 0;
//  return ptr;
//}
//
//void Controller::write(PGM_P body, ...) {
//	LOG("Write body part");
//	va_list args;
//	uint8_t *ptr = ether.tcpOffset();
//	uint8_t *start = ptr;
//
//	va_start(args, body);
//	for (;;) {
//		char c = pgm_read_byte(body++);
//
//		if (c == 0) break;
//		if (c != '%') {
//			*ptr++ = c;
//			continue;
//		}
//
//		c = pgm_read_byte(body++);
//		switch (c) {
//		case 'b':
//			strcpy((char *) ptr, va_arg(args, word) == 0 ? "false" : "true");
//			break;
//		case 's':
//			strcpy((char *) ptr, va_arg(args, const char *));
//			break;
//		case 'P': {
//			PGM_P s = va_arg(args, PGM_P);
//			char d;
//			while ((d = pgm_read_byte(s++)) != 0) *ptr++ = d;
//			continue;
//		}
//
//// 		case 'c':
//// 			Serial.print(va_arg(args, int));
//// 			break;
//		case 'd':
//			// sprintf((char *) ptr, "%d", va_arg(args, word));
//			wtoa(va_arg(args, word), (char *) ptr);
//			break;
//// 		case 'B':
//// 			Serial.print(va_arg(args, int), BIN);
//// 			break;
//		case 'l':
//			ltoa(va_arg(args, long), (char*) ptr, 10);
//			break;
//// 		case 'o':
//// 			Serial.print(va_arg(args, int), OCT);
//// 			break;
//		case 'x': {
//			char p1 = va_arg(args, word);
//			char p2;
//
//			p2 = (p1 >> 4) & 0x0F;
//			p1 = p1 & 0x0F;
//			if (p1 > 9) p1 += 0x07;
//			p1 += 0x30;
//			if (p2 > 9) p2 += 0x07;
//			p2 += 0x30;
//			*ptr++ = p2;
//			*ptr++ = p1;
//			continue;
//		}
//// 			Serial.print(va_arg(args, int), HEX);
//// 			break;
//// 		case 'f':
//// 			Serial.print(va_arg(args, double), DEC);
//// 			break;
//		default:
//			*ptr++ = c;
//			continue;
//		}
//		ptr += strlen((char *) ptr);
//	}
//	va_end(args);
//
//	ether.httpServerReply_with_flags(ptr - start, TCP_FLAGS_ACK_V);
//}
//
//void Controller::writeRaw(const char *s, uint16_t n) {
//	memcpy(ether.tcpOffset(), s, n);
//	ether.httpServerReply_with_flags(n, TCP_FLAGS_ACK_V);
//}
//
//void Controller::writeRawP(PGM_P p, uint16_t n) {
//	memcpy_P(ether.tcpOffset(), p, n);
//	ether.httpServerReply_with_flags(n, TCP_FLAGS_ACK_V);
//}
//
//void Controller::fin() {
//	LOG("Finalize response");
//	ether.httpServerReply_with_flags(0, TCP_FLAGS_ACK_V | TCP_FLAGS_FIN_V);
//}
//
//int Controller::getIntArg(const char *data, const char *key, int value) {
//   char temp[10];
//   if (ether.findKeyVal(data + 6, temp, sizeof temp, key) > 0) {
//       value = atoi(temp);
//   }
//   return value;
//}
