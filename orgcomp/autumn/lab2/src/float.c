#include <reg51.h>

unsigned long y;
unsigned long res1;

main(){

	
	y = (P0 & 0x0f) * 100 + ((P1 & 0xf0) >> 4) * 10 + (P1 & 0x0f);
	y = y << 16;
	y = y / 1000;
	if (P0 & 0x10) y *= -1;

	P2 = 0;
	if ( (long)y < 0 ) {
		y *= -1;
		P2 = 0xD0;
	}else P2 = 0xC0
	y = y * 10;
	P2 |= ((y & 0xf0000)) >> 16;
	y = (y & 0x0ffff) * 10;
	P3 = (y & 0xf0000) >> 12;
	P3 |= ((y & 0xffff) * 10) >> 16;
	
	while(1);
}
