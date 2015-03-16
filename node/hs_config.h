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
#define HS_CONFIG_TIME_UNIT_MINUTE  1
#define HS_CONFIG_TIME_UNIT_HOUR    2

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
    
    /**
     *  Server ip address.
     */
    uint8_t serverIp[4];
    
    /**
     *  Server UDP port.
     */
    uint16_t serverPort;
};

#define HS_CONFIG_PIN_TYPE_NONE     0
#define HS_CONFIG_PIN_TYPE_READ_D   1
#define HS_CONFIG_PIN_TYPE_READ_A   2
#define HS_CONFIG_PIN_TYPE_SWITCH   3
#define HS_CONFIG_PIN_TYPE_TEMP     4
#define HS_CONFIG_PIN_TYPE_HUM      5

#define HS_CONFIG_MAX_PIN_INDEX    31
#define HS_CONFIG_PIN_LENGTH       (HS_CONFIG_MAX_PIN_INDEX + 1)

struct HSPinConf {
    uint8_t number;
    uint8_t type;
    uint8_t config;
};

extern HSConfiguration hs_node_config;

/**
 *  Init global node configuration and pin configurations.
 */
void hs_config_init();

/**
 *  Store global node configuration and pin configurations.
 */
void hs_config_save();

/**
 *  Enter settings mode.
 */
void hs_config_enter_settings_mode();

/**
 *  Obtain pin value for specified pin configuration.
 *
 *  @param pinIndex pin configuration index
 *  @return pin value (digital, analog, temperature)
 */
uint16_t hs_config_get_pin_value(uint8_t pinIndex);

/**
 *  Get pin configuration on specified index.
 *
 *  @param pinIndex pin configuration index
 *  @return pin configuration reference
 */
HSPinConf *hs_config_get_pin(uint8_t pinIndex);

#endif /* defined(__Node__HSConfig__) */
