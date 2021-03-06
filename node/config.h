//#define DEBUG
#define NO_CRC_CHECK
//#define NO_RTC
//#define DHCPDEBUG
#define MEMORY_FREE

/**
 *  Size of ethernet buffer.
 */
#define HS_ETHERNET_BUF_SIZE    700

/**
 *  Default local UDP port.
 */
#define HS_DEF_UDP_LOCAL_PORT   12321

/**
 *  Default server UDP port.
 */
#define HS_DEF_UDP_SERVER_PORT  32123

#ifdef NO_RTC
#define HS_MAC_ADDRESS { 0x74, 0x69, 0x69, 0x2D, 0x30, 0x31 }
#endif

#define NET_CS_PIN              9

#define GENERATE_MAC_ADDRESS(rtcID) {0x50, 0x47, 0xE4, rtcID[4], rtcID[6], rtcID[7]}