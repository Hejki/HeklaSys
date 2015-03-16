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

#endif // DEBUG

#ifdef MEMORY_FREE
extern unsigned int __bss_end;
extern unsigned int __heap_start;
extern void *__brkval;

int freeMemory() {
    int free_memory;
    
    if((int)__brkval == 0)
        free_memory = ((int)&free_memory) - ((int)&__bss_end);
    else
        free_memory = ((int)&free_memory) - ((int)__brkval);
    
    return free_memory;
}
#endif // MEMORY_FREE