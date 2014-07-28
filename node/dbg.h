#ifndef _DBG_H_
#define _DBG_H

#include "config.h"

#ifdef DEBUG
class Dbg {
public:
    Dbg();
    
    void begin();
    void log(const char *fmt, ...) __attribute__((format(printf, 2, 3)));
};

#define dbg(fmt, ...) (Debug.log(fmt, ## __VA_ARGS__))

#else
class Dbg {
public:
    ﻿  Dbg();
    ﻿
    ﻿  void begin() {};
    ﻿  void log(const char *fmt, ...) __attribute__((format(printf, 2, 3))) {};
    ﻿
};

#define dbg(fmt, ...) ((void)0)

#endif

extern Dbg Debug;

#endif
