/**
************************************************************
* @file         user_main.c
* @brief        The program entry file
* @author       Gizwits
* @date         2017-07-19
* @version      V03030000
* @copyright    Gizwits
*
* @note         机智云.只为智能硬件而生
*               Gizwits Smart Cloud  for Smart Products
*               链接|增值ֵ|开放|中立|安全|自有|自由|生态
*               www.gizwits.com
*
***********************************************************/
#include "ets_sys.h"
#include "osapi.h"
#include "user_interface.h"
#include "gagent_soc.h"
#include "user_devicefind.h"
#include "user_webserver.h"
#include "gizwits_product.h"
#include "driver/hal_key.h"
#include "driver/Adafruit_NeoPixel.h"
#if ESP_PLATFORM
#include "user_esp_platform.h"
#endif

#ifdef SERVER_SSL_ENABLE
#include "ssl/cert.h"
#include "ssl/private_key.h"
#else
#ifdef CLIENT_SSL_ENABLE
unsigned char *default_certificate;
unsigned int default_certificate_len = 0;
unsigned char *default_private_key;
unsigned int default_private_key_len = 0;
#endif
#endif

/**@} */ 

/**@name Key related definitions 
* @{
*/
#define GPIO_KEY_NUM                            1                           ///< Defines the total number of key members
#define KEY_0_IO_MUX                            PERIPHS_IO_MUX_GPIO2_U      ///< ESP8266 GPIO function
#define KEY_0_IO_NUM                            2                           ///< ESP8266 GPIO number
#define KEY_0_IO_FUNC                           FUNC_GPIO2                  ///< ESP8266 GPIO name
#define KEY_1_IO_MUX                            PERIPHS_IO_MUX_MTMS_U       ///< ESP8266 GPIO function
#define KEY_1_IO_NUM                            14                          ///< ESP8266 GPIO number
#define KEY_1_IO_FUNC                           FUNC_GPIO14                 ///< ESP8266 GPIO name
LOCAL key_typedef_t * singleKey[GPIO_KEY_NUM];                              ///< Defines a single key member array pointer
LOCAL keys_typedef_t keys;                                                  ///< Defines the overall key module structure pointer    
/**@} */

int flag = 0;
/**
* Key1 key short press processing
* @param none
* @return none
*/
LOCAL void ICACHE_FLASH_ATTR key1ShortPress(void)
{
    GIZWITS_LOG("#### KEY1 short press ,Production Mode\n");
    
    gizwitsSetMode(WIFI_PRODUCTION_TEST);
	flag++;
	switch (flag) {
	//switch to blue
	case 1:
		currentDataPoint.valueRGB_DATA = 0x0000ff;
		//user handle
		setAllPixelColor(currentDataPoint.valueRGB_DATA);
		break;
	//switch to red
	case 2:
		currentDataPoint.valueRGB_DATA = 0xff0000;
		//user handle
		setAllPixelColor(currentDataPoint.valueRGB_DATA);
		break;
	//switch to green
	case 3:
		currentDataPoint.valueRGB_DATA = 0x00ff00;
		//user handle
		setAllPixelColor(currentDataPoint.valueRGB_DATA);
		break;
	case 4:
		gizwitsSetMode(WIFI_AIRLINK_MODE);
		flag = 0;
		break;
	}
}

/**
* Key1 key presses a long press
* @param none
* @return none
*/
LOCAL void ICACHE_FLASH_ATTR key1LongPress(void)
{
    GIZWITS_LOG("#### key1 long press, default setup\n");
    
    gizwitsSetMode(WIFI_RESET_MODE);
	currentDataPoint.valueRGB_DATA = 0x00ff00;
	//user handle
	setAllPixelColor(currentDataPoint.valueRGB_DATA);
	gizwitsSetMode(WIFI_AIRLINK_MODE);
}

/**
* Key2 key to short press processing
* @param none
* @return none
*/
LOCAL void ICACHE_FLASH_ATTR key2ShortPress(void)
{
    GIZWITS_LOG("#### key2 short press, soft ap mode \n");

    gizwitsSetMode(WIFI_SOFTAP_MODE);
}

/**
* Key2 button long press
* @param none
* @return none
*/
LOCAL void ICACHE_FLASH_ATTR key2LongPress(void)
{
    GIZWITS_LOG("#### key2 long press, airlink mode\n");
    
    gizwitsSetMode(WIFI_AIRLINK_MODE);
}

/**
* Key to initialize
* @param none
* @return none
*/
LOCAL void ICACHE_FLASH_ATTR keyInit(void)
{
    //singleKey[0] = keyInitOne(KEY_0_IO_NUM, KEY_0_IO_MUX, KEY_0_IO_FUNC,
    //                            key1LongPress, key1ShortPress);
    singleKey[0] = keyInitOne(KEY_1_IO_NUM, KEY_1_IO_MUX, KEY_1_IO_FUNC,
                                key2LongPress, key2ShortPress);
    keys.singleKey = singleKey;
    keyParaInit(&keys);
}

/**
* @brief user_rf_cal_sector_set

* Use the 636 sector (2544k ~ 2548k) in flash to store the RF_CAL parameter
* @param none
* @return none
*/
uint32_t ICACHE_FLASH_ATTR user_rf_cal_sector_set()
{
    return 636;
}

/**
* @brief program entry function

* In the function to complete the user-related initialization
* @param none
* @return none
*/
void ICACHE_FLASH_ATTR user_init(void)
{
	
    uint32_t system_free_size = 0;
	//wifi_set_opmode(0x01);	//����ΪSTATIONģʽ

    wifi_station_set_auto_connect(1);
    wifi_set_sleep_type(NONE_SLEEP_T);//set none sleep mode
    espconn_tcp_set_max_con(10);
    //uart_init(115200,115200);
    //UART_SetPrintPort(1);
    GIZWITS_LOG( "---------------SDK version:%s--------------\r\n", system_get_sdk_version());
    GIZWITS_LOG( "system_get_free_heap_size=%d\r\n",system_get_free_heap_size());

    keyInit();

	WS2812B_Init();
    gizwitsInit();  
    GIZWITS_LOG("--- system_free_size = %d ---\r\n", system_get_free_heap_size());
}
