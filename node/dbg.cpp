#include "Dbg.h"

#ifdef DEBUG
#include <Arduino.h>

#define DBG_MAX_LENGTH  80

void Dbg::begin() {
    Serial.begin(9600);
}

void Dbg::log(const char *fmt, ...) {
    char buf[DBG_MAX_LENGTH];
    
    va_list ap;
    va_start(ap, fmt);
    
    vsnprintf(buf, (DBG_MAX_LENGTH - 1), fmt, ap);
    va_end(ap);
    Serial.println(buf);
}

Dbg Debug;

#endif
