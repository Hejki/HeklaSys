#define DEBUG
#define NO_CRC_CHECK
//#define NO_RTC
//#define DHCPDEBUG

/**
 *  Size of ethernet buffer.
 */
#define HS_ETHERNET_BUF_SIZE    256

/**
 *  Default local UDP port.
 */
#define HS_DEF_UDP_LOCAL_PORT   12321

/**
 *  Default server UDP port.
 */
#define HS_DEF_UDP_SERVER_PORT  32123

/**
 *  Default server IP address.
 */
#define HS_DEF_UDP_SERVER_IP    {10,0,0,7}

#if NO_RTC
#define HS_MAC_ADDRESS { 0x74, 0x69, 0x69, 0x2D, 0x30, 0x31 }
#endif

#define NET_CS_PIN              8