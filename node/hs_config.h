//
//  hs_config.h
//  Node
//
//  Created by Hejki on 26.07.14.
//  Copyright (c) 2014 Hejki. All rights reserved.
//

#ifndef __Node__HSConfig__
#define __Node__HSConfig__

#include "common.h"

#define HS_CONFIG_TIME_UNIT_SECOND  0
#define HS_CONFIG_TIME_UNIT_MINUTE  0
#define HS_CONFIG_TIME_UNIT_HOUR    0

struct HSConfiguration {
    /**
     *  Interval for sending status information to server.
     */
    uint8_t statusSendInterval;
    
    /**
     *  Units for sending time interval.
     *  0 - seconds
     *  1 - minutes
     *  2 - hours
     */
    uint8_t statusSendTimeUnit;
    
    /**
     *  Number of supported pin positions.
     */
    uint8_t numberOfPinPositions;
};

#define HS_CONFIG_PIN_TYPE_NONE     0
#define HS_CONFIG_PIN_TYPE_READ_D   1
#define HS_CONFIG_PIN_TYPE_READ_A   2
#define HS_CONFIG_PIN_TYPE_SWITCH   3
#define HS_CONFIG_PIN_TYPE_TEMP     4
#define HS_CONFIG_PIN_TYPE_HUM      5

struct HSPinConf {
    uint8_t number;
    uint8_t type;
    uint8_t config;
};

/**
 *  Init configuration.
 */
void hs_config_init(HSConfiguration *config);

/**
 *  Read configuration for specified pin.
 *
 *  @param pinIndex  index of pin in configuration
 *  @param pinConfig configuration for pin
 */
void hs_config_read_pin(uint8_t pinIndex, HSPinConf *pinConfig);

extern const uint8_t hs_config_addr_pin[];

#endif /* defined(__Node__HSConfig__) */
