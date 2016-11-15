#include <reg51.h>

int main() {
	char aa, bb, cc, dd, ii = 0;
	for(;; ii++) {
		aa = ii & 0x08;
		bb = ii & 0x04;	
		cc = ii & 0x02;	
		dd = ii - 4;	
		P3 = ((aa > dd) & (bb != cc) || (dd >= cc));
	}
}
