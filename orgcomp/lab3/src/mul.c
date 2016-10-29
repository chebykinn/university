#include <reg51.h>
unsigned int a;
unsigned long s;

void main(){
	char i;
	a = P0 << 8;
	s = P1;

	for( i = 0; i < 8; i++ ) {
		s = (s & 1) ? (s + a) >> 1 : s >> 1;
	}
	P2 = s >> 8;
	P3 = s;
	while(1);
}
