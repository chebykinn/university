#include <reg51.h>

int main() {
	char aa, bb, cc, dd, ii = 0;
	for(;ii < 16; ii++) {
		if(ii == 8)
			P0 = P1;
		P1 <<= 1;
		aa = ii & 0x08;
		bb = ii & 0x04;	
		cc = ii & 0x02;	
		dd = ii - 4;	
		P1 |= ((aa > dd) & (bb != cc) || (dd >= cc));
	}
}
