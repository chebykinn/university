#include <reg51.h>
typedef unsigned char uint8_t;

uint8_t bin_to_bcd(uint8_t x){
	uint8_t mask = 0;
	if( (char)x < 0 ){
		// Sign bit
		mask = 0x80;
		x = -x;
	}
	return ((x / 10) << 4) | (x % 10) | mask;
}

uint8_t bcd_to_bin(uint8_t x){
    return ( ((x>>4) & 0x07) * 10 + (x & 0x0f) ) * (x & 0x80 ? -1 : 1);
}

void main(){
	uint8_t num = bcd_to_bin(P0);
    P1 = bin_to_bcd(num);
    while(1);
}
