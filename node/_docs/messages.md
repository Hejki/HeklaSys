* UDP protokol

### Zpravy

* Heartbeat
* registrace
* status
* write/read config

# Zpravy

### Format
`<typ><delka><id>[data zpravy]<CRC>`

* typ - typ zpravy, pro zpravy odpovedi je to pouze 0|1
* delka - delka dat zpravy (bez typu, id a CRC)
* id - ciselny identifikator zpravy, u odpovedi je stejny jako v dotazovaci zprave
* data zpravy - libovolna data zpravy, zavisi na typu zpravy
* CRC - kontrolni soucet

## 0 - OK
* pouze pro odpoved
* data jsou zavisla na prichozi zprave

## 1 - NOK
* pouze pro odpoved
* jedna se o chybovou zpravu
`<kod chyby>`

### Chyby
* 0 Unsupported message
* 1 Invalid checksum
* 2 No implemented message
* 3 Incorrect message implementation
* 4 Bad message length
* 5 Node must be in setting mode for call this message (post message #20 as first message)
* 6 Node cannot be in setting mode for this message (end setting mode by calling commit #26 or reset node)

## 2 - Node Info after redelayset (TODO not work)
* message from node with information about node setting
* data same as response to message #11

## 3 - Pin Value
* message from node with information about pin value (based on pin configuration)
* data: `<PIN_INDEX><VAL_L><VAL_H>`
** PIN_INDEX - index of configuration pin
** VAL - 2 byte value of pin


## 4-9 reserved

## 10 - Ping
_Request:_ empty

_Response:_ OK (empty)

## 11 - Get Local Info
_Request:_ empty

_Response:_ OK (localip, locaport) 

`<ip1><ip2><ip3><ip4><portL><portH>`
* IP - local ip address bytes (ip1.ip2.ip3.ip4)
* PORT - 2bytes of local port

## 20 - Enter Setting Mode
_Request:_ empty

* Prepare for configuration set
* Disable auto pooling

_Response:_ OK (empty)

## 21 - Get Node Settings
_Request:_ empty

_Response (9):_ OK `<INT><INT_UNIT><NUM_PIN><IP1><IP2><IP3><IP4><PORT_L><PORT_H>`
	
* INT - interval for sending status information to server
* INT_UNIT - time unit for sending interval (0-seconds, 1-minutes, 2-hours)
* NUM_PIN - number of supported pin positions (see message 15, 16)
* IP - server ip address bytes (ip1.ip2.ip3.ip4)
* PORT - server UDP port

## 22 - Set Node Settings
_Request (9):_ `<INT><INT_UNIT><NUM_PIN><IP1><IP2><IP3><IP4><PORT_L><PORT_H>`
	
* param description is on message #21 - Get Node Settings

_Response:_ OK (same as #21)

## 23 - Get Pin Settings
_Request:_ `<PIN_INDEX>`

* PIN_INDEX - index of pin configuration (currently supports 32 pin positions, index: 0 - 31)

_Response:_ OK `<NUM><TYPE><CONF>`

* NUM - number identifier of pin
* TYPE - pin type
	- 0 (NONE) pin will be ignored during processing pins
	- 1 (READ\_D) used for reading digital values
	- 2 (READ\_A) used for reading analog values
	- 3 (SWITCH) used for switching another digital pin
	- 4 (TEMP) temperature sensor pin
	- 5 (HUM) humidity sensor pin
* CONF - additional configuration (options are based on pin type)
	- type: 3 conf 0-3 bit value of dependent pin index
	- type: 3 conf 4 bit HIGH on pin sets HIGH on depend pin; otherwise set LOW on depend pin (for pin HIGH value)

## 24 - Set Pin Settings
_Request:_ `<PIN_INDEX><NUM><TYPE><CONF>`
	
* detailed data description, see #23 - Get Pin Settings
* PIN_INDEX - index of pin configuration
* NUM - number of pin
* TYPE - pin type
* CONF - additional configuration

_Response:_ OK (same as #23)

## 25 - Clear Pin Settings
_Request:_ empty

* Clear all pin configurations

_Response:_ OK (empty)

## 26 - Commit Settings
_Request:_ empty

* Store all pin and node configurations

_Response:_ OK (empty)

## 30 - Digital Read
_Request:_ `<PIN>`
	
* PIN - digital pin number for reading value

_Response:_ OK `<VAL>`
	
* VAL - value of pin (0|1)

## 31 - Digital Write
_Request:_ `<PIN><VAL>`
	
* PIN - digital pin number for set
* VAL - value for specified pin number (0|1)

_Response:_ OK `<VAL>`
	
* VAL - previous set value of pin

## 32 - Analog Read
_Request:_ `<PIN>`
	
* PIN - number of analog pin for reading value

_Response:_ OK `<valL><valH>`
	
* VAL - value of analog pin (2byte value: 0-1023)

## 33 - Analog Write
_Request:_ `<PIN><valL><valH>`
	
* PIN - number of PWM digital pin for set value
* VAL - value to set (2byte)

_Response:_ OK (empty)


