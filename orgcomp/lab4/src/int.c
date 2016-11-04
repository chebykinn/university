#include <stdio.h>

typedef unsigned int uint16_t;
typedef unsigned char uint8_t;

int main(){
	uint16_t num = 0, x, m = 100;
    for( x = 0; x < 100; x++ ) {
    	num = 1;
		num = ( m - 5 * x / 9 ) / m;
		num = ( m - x / 2 * num ) / m;
		num = ( m - x / 4 * num ) / m;
		num = x / 2 * num;
		P3 = num;
    }
	while(1);
}
