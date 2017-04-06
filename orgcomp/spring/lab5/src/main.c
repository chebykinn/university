#include <reg51.h>
float numb;
char digit;
char xdata mas[8];
char m = 1;
char mm;
int i = 0;

void get_char(void) interrupt 0 {
	char x = P3;
	switch (x) {
		case 0x7b: digit = '1'; break;
		case 0x79: digit = '2'; break;
		case 0x78: digit = '3'; break;
		case 0xbb: digit = '4'; break;
		case 0xb9: digit = '5'; break;
		case 0xb8: digit = '6'; break;
		case 0xdb: digit = '7'; break;
		case 0xd9: digit = '8'; break;
		case 0xd8: digit = '9'; break;
		case 0xeb: digit = '0'; break;
		case 0xe9: digit = ','; break;
		case 0xe8: digit = 'e'; break;
		case 0xc8: digit = '-'; break;
		default: digit = 0xff;
	}

	if ( digit == ',' ) {
		m = 1;
		mas[i++] = digit;
		return;
	}

	if ( digit == '-' ) {
		numb = -numb;
		mas[i++] = digit;
		return;
	}

	if ( digit == 'e' ) {
		if( m ) numb = numb / m;
		i = 0;
		m = 0;
	} else {
		mas[i++] = digit;
		numb = numb * 10 + (digit & 0xf);

		if ( m ) m *= 10;
		
	}

}

void main() {
	EX0 = 1;
	IT0 = 1;
	EA = 1;
	while (1);
}
