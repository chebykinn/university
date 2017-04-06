#include <reg51.h>

int main(){
	unsigned int num, x, m;
	m = 255;
	while(1)
    	for( x = 0; x < 255; x++ ) {
			num = m - ((x * 142) >> 8);
			num = m - ((x * num) >> 9);
			num = m - ((x * num) >> 10);
			num = ((x * num) >> 9);
			P3 = num;
    	}
}
