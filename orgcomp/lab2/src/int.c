#include <reg51.h>
typedef unsigned char uint8_t;
typedef unsigned int uint16_t;

void bin_to_bcd(uint16_t x, uint8_t *x0, uint8_t *x1) {
	uint16_t result = 0;
	uint8_t sign = 0;
	if((int)x < 0) {
		sign = 0xD0;
		x *= -1;
	}else sign = 0xC0;

	*x1 =  (((x / 10) % 10) << 4) | (x % 10);
	x = x / 100;
	*x0 =  x | sign;
}

uint16_t bcd_to_bin(uint8_t x0, uint8_t x1) {
    int result = ( ((x1 >> 4)) * 10 + (x1 & 0x0f) );
    return (result + (x0 & 0x0f) * 100) * ((x0 & 0xD0) ? -1 : 1);
}

void main(){
	uint16_t num = bcd_to_bin(P0, P1);
	uint8_t p2, p3;
    bin_to_bcd(num, &p2, &p3);
    P2 = p2;
    P3 = p3;
	while(1);
}
