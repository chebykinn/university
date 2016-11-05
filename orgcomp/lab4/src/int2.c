#include <reg51.h>

int main(){
	int num, x, m;	  
	m = 100;
	while(1)
    	for( x = 0; x < 100; x++ ) {
			num = m - (((x >> 8) * 142) >> 8);
			num = m - ((x * num) >> 9);
			num = m - ((x * num) >> 10);
			num = m - ((x * num) >> 9);
			P3 = num;
    	}
}
