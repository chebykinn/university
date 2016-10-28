#include <reg51.h>

unsigned int s, a;

void main() {
	
	char i;
	
	s = P1 << 8; // 0xbbbb 0000
	a = P2 << 8; // 0xaaaa 0000
	
	for( i = 0; i < 8; i++ )
		s = ((s<<1) - a < 0 ) ? (s<<1 ) : ((s<<1)-a + 1); 
	
	P3 = s>>8;
	while(1);
}
