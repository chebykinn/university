#include <reg51.h>
unsigned int a;
unsigned long s;

void main(){
	char i;
	a = P0;	 // 0x0000 aaaa
	a <<= 8; // 0xaaaa 0000
	s = P1;  // 0x0000 0000 bbbb

	for( i = 0; i < 8; i++ ) {
			if( s & 0x0001 )
				s += a;
			s >>= 1;
	}
	P2 = s;
	P3 = s >> 8;
	while(1);
}
