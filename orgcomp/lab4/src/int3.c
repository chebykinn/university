#include <reg51.h>

int main(){
	unsigned int num, x, m;
	m = 255;
	while(1)
    	for( x = 0; x < 255; x++ ) {
			num = ( m - (5 * x) / 9 );
			num = ( m - ((x * num) / 2) >> 8);
			num = ( m - ((x * num) / 4) >> 8);
			num = ((x * num) / 2) >> 8;
			P3 = num;
    	}
}
