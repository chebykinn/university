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
void main( void ) {
	unsigned char animation_counter = 0x07;
	unsigned char left = 0xC0;
	unsigned char right = 0x07;
	unsigned char cnt = 0;


	while(1) {
		unsigned char dip_value = dip();

		if(dip_value == LAB_DIP_VALUE) {

			leds(0x00);
			if(cnt < 6){
				leds((left >> cnt) | (right << cnt));
			}else{
				leds((left >> (12 - cnt)) | (right << (12 - cnt)));
			}
			cnt++;
			if(cnt > 12) cnt = 0;
		} else {
			leds(~dip_value);
			animation_counter = 0x07;
		}

		delay(100);
	}
}
