#ifndef _DBG_H_
#define _DBG_H

#include "config.h"

#ifdef DEBUG
class Dbg {
public:
    Dbg() {}
    
    void begin();
    void log(const char *fmt, ...) __attribute__((format(printf, 2, 3)));
};

#define dbg(fmt, ...) (Debug.log(fmt, ## __VA_ARGS__))

extern Dbg Debug;

#else

#define dbg(fmt, ...) ((void)0)

#endif // DEBUG


#ifdef MEMORY_FREE
#ifdef __cplusplus
extern "C" {
#endif
    
    int freeMemory();
    
#ifdef  __cplusplus
}
#endif
#endif // MEMORY_FREE

#endif // _DBG_H_
