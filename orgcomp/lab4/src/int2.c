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

/*
unsigned int x,y, S;
main() {
	while(1)
		for(x=0; x<0xff; x++) { 
			y=(x*x)>>8; S=0xff; // y=(x*x)/m, S0=m
			S = 0xff- (((y*S)>>8)*6)>>8; //S1=(m –( x2/m*a1/m *S0 )/m )
			S = 0xff- (((y*S)>>8)*13)>>8; //S1=(m –( x2/m*a2/m*S0 )/m
			S = 0xff- (((y*S)>>8)*43)>>8; //S1=(m –( x2/m*43/m *S0 )/m
			S = (S*x)>>8;
		}
		*/
