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

## 2 - Heartbeat
* pro informaci ze zarizeni stale bezi


## 3-9 rezervovano

## 10 - Ping
* ocekava prichozi zpravu

## 11 - Set Server Settings
`<IP><IP><IP><IP><port>`
* IP - nastaveni IP adresy serveru
* port - UDP port na serveru
* Ocekava se OK prazdna odpoved

## 12 - Get Node Settings

## 13 - Set Node Setting

## 20 - Digital Read
`<PIN>`
* PIN - pin z ktereho chci precist hodnotu
* Odpovedi je 0|1

## 21 - Digital Write
`<PIN><0|1>`
* PIN - pin kterych chci nastavit
* 0|1 - hodnota digitalniho pinu
* Prazdna odpoved

## 22 - Analog Read
`<PIN>`
* PIN - oznaceni pinu z ktereho budu cist hodnotu
* Odpoved je cislo 0-1023 (uint16)