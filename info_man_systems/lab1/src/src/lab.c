#include "aduc812.h"
#include "led.h"
#include "max.h"

void delay ( unsigned long ms ) {
	volatile unsigned long i, j;
	for( j = 0; j < ms; j++ ) {
		for( i = 0; i < 50; i++ );
	}
}

unsigned char dip() {
	return read_max(EXT_LO);
}

#define LAB_DIP_VALUE 0x44
#define MAX_TICKS 6
#define LEFT 0xC0
#define RIGHT 0x07
void main( void ) {
	unsigned char left = 0xC0;
	unsigned char right = 0x07;
	unsigned char cnt = 0;

	while(1) {
		unsigned char dip_value = dip();

		if(dip_value == LAB_DIP_VALUE) {

			if(cnt < MAX_TICKS){
				leds((LEFT >> cnt) | (RIGHT << cnt));
			}else{
				leds((LEFT >> (MAX_TICKS * 2 - cnt)) | (RIGHT << (MAX_TICKS * 2 - cnt)));
			}
			cnt++;
			if(cnt > MAX_TICKS * 2) cnt = 0;
		} else {
			leds(~dip_value);
		}

		delay(100);
	}
}
