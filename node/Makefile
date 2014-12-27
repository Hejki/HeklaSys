### For detailled explanations about all the avalaible options,
### please refer to https://github.com/sudar/Arduino-Makefile/blob/master/arduino-mk-vars.md

### This is the path to where you have created/cloned your project
PROJECT_DIR       = $(CURDIR)

### Path to the Arduino-Makefile directory.
ARDMK_DIR         = $(PROJECT_DIR)/make

### Path to the Arduino application and ressources directory.
ARDUINO_DIR       = /Applications/Arduino.app/Contents/Java

### Path to where the your project's libraries are stored.
USER_LIB_PATH     :=  $(PROJECT_DIR)/lib

### It must be set to the board you are currently using. (i.e uno, mega2560, etc.)
BOARD_TAG         = uno
MONITOR_BAUDRATE  = 115200

### Path to the AVR tools directory such as avr-gcc, avr-g++, etc.
AVR_TOOLS_DIR     = /usr/local

### Path to avrdude directory.
AVRDDUDE          = /opt/local/bin/avrdude
#AVRDUDE_OPTS	  = -v
AVRDUDE_ARD_BAUDRATE= 115200
AVRDUDE_ARD_PROGRAMMER= arduino

### Flags you might want to set for debugging purpose. Comment to stop.
CPPFLAGS          = -Wall -Wextra -std=c++11
#-Wno-variadic-macros

### The port your board is connected to. Using an '*' tries all the ports and finds the right one.
MONITOR_PORT      = /dev/tty.usbserial*
#MONITOR_PORT 	  = /dev/ttyUSB0

### don't touch this
CURRENT_DIR       = $(shell basename $(CURDIR))

### This is were you put the binaries you just compile using 'make'
OBJDIR            = $(PROJECT_DIR)/build
TARGET			  = HeklaSysNode
OPTIMIZATION_LEVEL= s
#RESET_CMD		  = stty -f $(MONITOR_PORT) hupcl
# ARDUINO_QUIET     = 1
ARDUINO_LIBS	  = Time EtherCard MCP79412RTC Wire OneWire DallasTemperature

### path to Arduino.mk, inside the ARDMK_DIR, don't touch.
include $(ARDMK_DIR)/Arduino.mk

