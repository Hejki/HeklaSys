//
//  hs_config.c
//  Node
//
//  Created by Hejki on 26.07.14.
//  Copyright (c) 2014 Hejki. All rights reserved.
//
#include <DallasTemperature.h>

#include "hs_config.h"
#include <avr/eeprom.h> // http://www.nongnu.org/avr-libc/user-manual/group__avr__eeprom.html

const static uint8_t hs_config_addr_config = 0;

const static uint8_t hs_config_addr_pin_0  = hs_config_addr_config + sizeof(HSConfiguration);
const static uint8_t hs_config_addr_pin_1  = hs_config_addr_pin_0 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_2  = hs_config_addr_pin_1 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_3  = hs_config_addr_pin_2 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_4  = hs_config_addr_pin_3 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_5  = hs_config_addr_pin_4 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_6  = hs_config_addr_pin_5 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_7  = hs_config_addr_pin_6 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_8  = hs_config_addr_pin_7 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_9  = hs_config_addr_pin_8 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_10 = hs_config_addr_pin_9 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_11 = hs_config_addr_pin_10 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_12 = hs_config_addr_pin_11 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_13 = hs_config_addr_pin_12 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_14 = hs_config_addr_pin_13 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_15 = hs_config_addr_pin_14 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_16 = hs_config_addr_pin_15 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_17 = hs_config_addr_pin_16 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_18 = hs_config_addr_pin_17 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_19 = hs_config_addr_pin_18 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_20 = hs_config_addr_pin_19 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_21 = hs_config_addr_pin_20 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_22 = hs_config_addr_pin_21 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_23 = hs_config_addr_pin_22 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_24 = hs_config_addr_pin_23 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_25 = hs_config_addr_pin_24 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_26 = hs_config_addr_pin_25 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_27 = hs_config_addr_pin_26 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_28 = hs_config_addr_pin_27 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_29 = hs_config_addr_pin_28 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_30 = hs_config_addr_pin_29 + sizeof(HSPinConf);
const static uint8_t hs_config_addr_pin_31 = hs_config_addr_pin_30 + sizeof(HSPinConf);

const static uint8_t hs_config_addr_pin[] = {
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

const static uint8_t _hsConfigAddressOffset = 1;
static HSPinConf hs_pin_configurations[HS_CONFIG_PIN_LENGTH];
static void *hs_pin_config_objects[HS_CONFIG_PIN_LENGTH];

HSConfiguration hs_node_config; // global

inline static uint8_t hs_config_addr(const uint8_t configAddress) {
    return _hsConfigAddressOffset + configAddress;
}

/**
 *  Read configuration for specified pin.
 *
 *  @param pinIndex  index of pin in configuration
 *  @param pinConfig configuration for pin (out)
 */
static void _hs_config_read_pin(uint8_t pinIndex, HSPinConf *pinConfig) {
    eeprom_busy_wait();
    
    eeprom_read_block(pinConfig,
                      (const void *) hs_config_addr(hs_config_addr_pin[pinIndex]),
                      sizeof(HSPinConf));
}

/**
 *  Save configuration for specified pin.
 *
 *  @param pinIndex  index of pin in configuration
 *  @param pinConfig configuration for pin (in)
 */
static void _hs_config_save_pin(uint8_t pinIndex, const HSPinConf *pinConfig) {
    eeprom_busy_wait();
    
    eeprom_write_block(pinConfig,
                       (void *) hs_config_addr(hs_config_addr_pin[pinIndex]),
                       sizeof(HSPinConf));
}

static void _hs_config_objects_init() {
    for (uint8_t i = 0; i < hs_node_config.numberOfPinPositions; i++) {
        HSPinConf pin = hs_pin_configurations[i];
        
        if (pin.type == HS_CONFIG_PIN_TYPE_TEMP) {
            DallasTemperature *sensors = new DallasTemperature(pin.number);
            
            sensors->begin();
            hs_pin_config_objects[i] = sensors;
        }
    }
    
    status.settingsMode = false;
}

void hs_config_init() {
    eeprom_busy_wait();
    
//    _hsConfigAddressOffset = eeprom_read_byte(0);
    
    eeprom_read_block(&hs_node_config,
                      (const void *) hs_config_addr(hs_config_addr_config),
                      sizeof(HSConfiguration));
    
    for (uint8_t i = 0; i < hs_node_config.numberOfPinPositions; i++) {
        _hs_config_read_pin(i, &hs_pin_configurations[i]);
    }
    for (uint8_t i = 0; i < HS_CONFIG_PIN_LENGTH; i++) {
        hs_pin_config_objects[i] = 0;
    }
    
    _hs_config_objects_init();
}

void hs_config_save() {
    eeprom_busy_wait();
    
    eeprom_write_block(&hs_node_config,
                       (void *) hs_config_addr(hs_config_addr_config),
                       sizeof(HSConfiguration));
    
    for (uint8_t i = 0; i < hs_node_config.numberOfPinPositions; i++) {
        _hs_config_save_pin(i, &hs_pin_configurations[i]);
    }
    
    _hs_config_objects_init();
}

void hs_config_enter_settings_mode() {
    status.settingsMode = true;
    
    for (uint8_t i = 0; i < hs_node_config.numberOfPinPositions; i++) {
        HSPinConf pin = hs_pin_configurations[i];
        if (pin.type == HS_CONFIG_PIN_TYPE_TEMP) {
            delete (DallasTemperature *) hs_pin_config_objects[i];
        }
    }
    
    for (uint8_t i = 0; i < HS_CONFIG_PIN_LENGTH; i++) {
        hs_pin_config_objects[i] = 0;
    }
}

uint16_t hs_config_get_pin_value(uint8_t pinIndex) {
    HSPinConf pin = hs_pin_configurations[pinIndex];
    
    switch (pin.type) {
        case HS_CONFIG_PIN_TYPE_READ_D:
            return digitalRead(pin.number);
        case HS_CONFIG_PIN_TYPE_READ_A:
            return analogRead(pin.number);
        case HS_CONFIG_PIN_TYPE_TEMP: {
            DallasTemperature *sensors = (DallasTemperature *) hs_pin_config_objects[pinIndex];
            
            if (sensors) {
                float sum = 0;
                uint8_t sensorCnt = sensors->getDeviceCount();
                
                sensors->requestTemperatures();
                for (uint8_t i = 0; i < sensorCnt; i++) {
                    sum += sensors->getTempCByIndex(i);
                }
                
                return int((sum / sensorCnt) * 100);
            }
        }
    }
    return 0;
}

HSPinConf *hs_config_get_pin(uint8_t pinIndex) {
    return &hs_pin_configurations[pinIndex];
}