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

## 2 - Pin Value
* message from node with information about pin value (based on pin configuration)
* data: 2 byte value

## 3 - Node Info after reset
* message from node with information about node setting
* data same as response to message 17

## 4-9 reserved

## 10 - Ping
_Request:_ empty

_Response:_ OK (empty)

## 11 - Get Server Settings
_Request:_ empty

_Response:_ OK `<ip1><ip2><ip3><ip4><portL><portH>`
* IP - server ip address bytes (ip1.ip2.ip3.ip4)
* PORT - 2bytes of server port

## 12 - Set Server Settings
_Request:_ `<ip1><ip2><ip3><ip4><portL><portH>`

* IP - server ip address bytes
* PORT - server UDP port

_Response:_ OK (empty)

## 13 - Get Node Settings
_Request:_ empty

_Response:_ OK `<INT><INT_UNIT><NUM_PIN>`
	
* INT - interval for sending status information to server
* INT_UNIT - time unit for sending interval (0-seconds, 1-minutes, 2-hours)
* NUM_PIN - number of supported pin positions (see message 15, 16)

## 14 - Set Node Settings
_Request:_ `<INT><INT_UNIT><NUM_PIN>`
	
* param description is on message 13 - Get Node Settings

_Response:_ OK (empty)

## 15 - Get Pin Settings
_Request:_ `<PIN_INDEX>`

* PIN_INDEX - index of pin configuration (actually supports 32 pin positions, index: 0 - 31)

_Response:_ OK `<NUM><TYPE><CONF>`

* NUM - number identfier of pin
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

## 16 - Set Pin Settings
_Request:_ `<PIN_INDEX><NUM><TYPE><CONF>`
	
* detailed data description, see 15 - Get Pin Settings
* PIN_INDEX - index of pin configuration
* NUM - number of pin
* TYPE - pin type
* CONF - additional configuration

_Response:_ OK (empty)

## 17 - Get Info
_Request:_ empty

_Response:_ OK (localip, locaport) 

`<ip1><ip2><ip3><ip4><portL><portH>`
* IP - server ip address bytes (ip1.ip2.ip3.ip4)
* PORT - 2bytes of server port

## 20 - Digital Read
_Request:_ `<PIN>`
	
* PIN - digital pin number for reading value

_Response:_ OK `<VAL>`
	
* VAL - value of pin (0|1)

## 21 - Digital Write
_Request:_ `<PIN><VAL>`
	
* PIN - digital pin number for set
* VAL - value for specified pin number (0|1)

_Response:_ OK `<VAL>`
	
* VAL - previous set value of pin

## 22 - Analog Read
_Request:_ `<PIN>`
	
* PIN - number of analog pin for reading value

_Response:_ OK `<valL><valH>`
	
* VAL - value of analog pin (2byte value: 0-1023)

## 23 - Analog Write
_Request:_ `<PIN><valL><valH>`
	
* PIN - number of PWM digital pin for set value
* VAL - value to set (2byte)

_Response:_ OK (empty)
