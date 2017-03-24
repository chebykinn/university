#include <reg51.h>
long s;
unsigned int a;

void main() {
	
	char i;
	
	s = (P0 << 8)|P1; 
	a = P2 << 8; 
	for( i = 0; i < 8; i++ )
		s = ((s<<1) - a < 0 ) ? (s<<1 ) : ((s<<1)-a + 1); 
	
	P3 = s;
	while(1);
}
