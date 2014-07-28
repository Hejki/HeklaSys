//
//  hs_config.c
//  Node
//
//  Created by Hejki on 26.07.14.
//  Copyright (c) 2014 Hejki. All rights reserved.
//

#include "hs_config.h"
#include <avr/eeprom.h> // http://www.nongnu.org/avr-libc/user-manual/group__avr__eeprom.html

const uint8_t hs_config_addr_config = 0;

const uint8_t hs_config_addr_pin_0  = hs_config_addr_config + sizeof(HSConfiguration);
const uint8_t hs_config_addr_pin_1  = hs_config_addr_pin_0 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_2  = hs_config_addr_pin_1 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_3  = hs_config_addr_pin_2 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_4  = hs_config_addr_pin_3 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_5  = hs_config_addr_pin_4 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_6  = hs_config_addr_pin_5 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_7  = hs_config_addr_pin_6 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_8  = hs_config_addr_pin_7 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_9  = hs_config_addr_pin_8 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_10 = hs_config_addr_pin_9 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_11 = hs_config_addr_pin_10 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_12 = hs_config_addr_pin_11 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_13 = hs_config_addr_pin_12 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_14 = hs_config_addr_pin_13 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_15 = hs_config_addr_pin_14 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_16 = hs_config_addr_pin_15 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_17 = hs_config_addr_pin_16 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_18 = hs_config_addr_pin_17 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_19 = hs_config_addr_pin_18 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_20 = hs_config_addr_pin_19 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_21 = hs_config_addr_pin_20 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_22 = hs_config_addr_pin_21 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_23 = hs_config_addr_pin_22 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_24 = hs_config_addr_pin_23 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_25 = hs_config_addr_pin_24 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_26 = hs_config_addr_pin_25 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_27 = hs_config_addr_pin_26 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_28 = hs_config_addr_pin_27 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_29 = hs_config_addr_pin_28 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_30 = hs_config_addr_pin_29 + sizeof(HSPinConf);
const uint8_t hs_config_addr_pin_31 = hs_config_addr_pin_30 + sizeof(HSPinConf);

const uint8_t hs_config_addr_pin[] = {
    hs_config_addr_pin_0, hs_config_addr_pin_1, hs_config_addr_pin_2,
    hs_config_addr_pin_3, hs_config_addr_pin_4, hs_config_addr_pin_5,
    hs_config_addr_pin_6, hs_config_addr_pin_7, hs_config_addr_pin_8,
    hs_config_addr_pin_9, hs_config_addr_pin_10, hs_config_addr_pin_11,
    hs_config_addr_pin_12, hs_config_addr_pin_13, hs_config_addr_pin_14,
    hs_config_addr_pin_15, hs_config_addr_pin_16, hs_config_addr_pin_17,
    hs_config_addr_pin_18, hs_config_addr_pin_19, hs_config_addr_pin_20,
    hs_config_addr_pin_21, hs_config_addr_pin_22, hs_config_addr_pin_23,
    hs_config_addr_pin_24, hs_config_addr_pin_25, hs_config_addr_pin_26,
    hs_config_addr_pin_27, hs_config_addr_pin_28, hs_config_addr_pin_29,
    hs_config_addr_pin_30, hs_config_addr_pin_31
};

const uint8_t _hsConfigAddressOffset = 1;

inline uint8_t hs_config_addr(const uint8_t configAddress) {
    return _hsConfigAddressOffset + configAddress;
}

void hs_config_init(HSConfiguration *config) {
    eeprom_busy_wait();
    
//    _hsConfigAddressOffset = eeprom_read_byte(0);
    
    eeprom_read_block(config,
                      (const void *) hs_config_addr(hs_config_addr_config),
                      sizeof(HSConfiguration));
}

void hs_config_read_pin(uint8_t pinIndex, HSPinConf *pinConfig) {
    eeprom_busy_wait();
    
    eeprom_read_block(pinConfig,
                      (const void *) hs_config_addr(hs_config_addr_pin[pinIndex]),
                      sizeof(HSPinConf));
}