#include <reg51.h>

char bdata mem; //бит-адресуемая переменная
sbit x1 = mem^0; //биты двоичного набора
sbit x2 = mem^1;
sbit x3 = mem^2;
sbit x4 = mem^3;
sbit z = P1^0;
int main() {
	for(mem = 0; mem < 16; mem++) {
		if(mem == 8)
			P0 = P1;
		P1 <<= 1;
		z = !x1 & (x2 | !x3) | x1 & x4;
	}
}
