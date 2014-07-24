///*
// * Controller.h
// *
// *  Created on: 19. 1. 2014
// *      Author: hejki
// */
//
//#ifndef CONTROLLER_H_
//#define CONTROLLER_H_
//
//#include <avr/pgmspace.h>
//
///**
// * Controller class defines behaviour for manipulate with HTTP request.
// */
//class Controller {
//public:
//	virtual void process(const char *request);
//
//	void header(int status);
//	void write(PGM_P body, ...);
//	void writeRaw(const char *s, uint16_t n);
//	void writeRawP(PGM_P p, uint16_t n);
//	void flush();
//
//	int getIntArg(const char *data, const char *key, int value = -1);
//};
//
//#endif /* CONTROLLER_H_ */
