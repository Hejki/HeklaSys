#include "rtc.h"

void log(const char *fmt, ...) {
    char buf[80];
    
    va_list ap;
    va_start(ap, fmt);
    
    vsnprintf(buf, (80 - 1), fmt, ap);
    va_end(ap);
    Serial.println(buf);
}

void setup() {
  Serial.begin(9600);
  
  if (!RTC.isRunning()) {
        Serial.println("RTC was not running set default time");
        setTime(0, 0, 0, 1, 1, 0); // 1.1.2000 00:00:00
        RTC.set(now());
        RTC.vbaten(true);
    }
    
    setSyncProvider(RTC.get);
    if (timeStatus() != timeSet) {
        Serial.println("Unable to sync with the RTC");
        return;
    }
    
    uint8_t rtcId[8];
    RTC.idRead(rtcId);
    log("MAC: %x:%x:%x:%x:%x:%x:%x:%x", rtcId[0], rtcId[1], rtcId[2], rtcId[3], rtcId[4], rtcId[5], rtcId[6], rtcId[7]);
}

void loop() {
  
}


