#ifndef HSCOMMON_H_
#define HSCOMMON_H_

#include <Arduino.h>
#include <stdint.h>
#include <Time.h>
#include "config.h"
#include "dbg.h"

struct DeviceState {
    time_t lastSendTime;
    bool settingsMode;
};

extern DeviceState status;

#endif /* HSCOMMON_H_ */