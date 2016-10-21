#include <reg51.h>
typedef unsigned char uint8_t;
typedef unsigned int uint16_t;

uint16_t bin_to_bcd( uint16_t x ) {
	// Sign bit
    uint8_t mask = x & 0x80 ? 0x80 : 0;
    x *= 10;
    return ((x & 0x700) >> 4) | ((((x & 0xff) * 10) & 0xf00) >> 8) | mask;
}

uint16_t bcd_to_bin( uint16_t x ) {
    return (((((x & 0x70) >> 4) * 10 + (x & 0x0f)) << 8) / 100)*(x & 0x80 ? -1 : 1);
}

void main() {
    uint16_t num = bcd_to_bin(P0);
    P1 = bin_to_bcd(num);
    while(1);
}
