#ifndef __SERIAL__H
#define __SERIAL__H

const char EOL[3] = { 0xD, 0xA, 0x00 };
#define S9600   0xFD
#define S4800   0xFA
#define S2400   0xF4
#define S1200 0xE8

// declarations
// interrupt
void UART_INT_init();
// serial
void uart_s_init(int speed);
unsigned char uart_s_read_ready();
void uart_s_write(unsigned char c);
unsigned char uart_s_read();
void type(char * str);


void uart_s_init(int speed) {
	TH1 = speed;   
	TMOD |= 0x20; // Таймер 1 будет работать в режиме autoreload
	TR1 = 1;      // start T1
	SCON = 0x50;  // REN = 1, UART mode 1
}

unsigned char uart_s_read_ready() {
	return RI;
}

void uart_s_write(unsigned char c) {
	SBUF = c;
	TI = 0;
	while(!TI);
}

unsigned char uart_s_read() {
	while(!RI);
	RI = 0;
	return SBUF;
}

void type(char* str) {
	while(*str)
		uart_s_write(*str++);
}

#endif //__SERIAL__H
