#include <reg51.h>
float numb;
char digit;
char xdata mas[8];
char m = 1;
char mm;
int i = 0, j = 0;

char codes[] = { 0xdd, 0x7b, 0x7d, 0xde, 0x7e, 0xb7, 0xe7 }; // -12,34

void get_char(void) interrupt 0 {
	char x = codes[j++];
	switch (x) {
		case 0x77: digit = '0'; break;
		case 0x7b: digit = '1'; break;
		case 0x7d: digit = '2'; break;
		case 0x7e: digit = '3'; break;
		case 0xb7: digit = '4'; break;
		case 0xbb: digit = '5'; break;
		case 0xbd: digit = '6'; break;
		case 0xbe: digit = '7'; break;
		case 0xd7: digit = '8'; break;
		case 0xdb: digit = '9'; break;
		case 0xdd: digit = '-'; break;
		case 0xde: digit = ','; break;
		case 0xe7: digit = 'e'; break;
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
