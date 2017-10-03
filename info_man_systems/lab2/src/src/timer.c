#include <timer.h>
#include <aduc812.h>
#include <stdint.h>
#include <max.h>
#include <led.h>

const uint16_t notes[] = {
	493,
	440,
	391,
	349,
	329,
	293,
	261,
};

unsigned long ms_count;

unsigned long last_note_swap;

uint8_t current_ena;
uint8_t current_note;

uint8_t note_delay[2];
uint8_t time_delay[2];

void set_vector(unsigned char xdata* address, void* vector);
void set_ena(uint8_t value);

void compute_note_delay(uint8_t index);
void compute_timer_delay();

void set_vector(uint8_t xdata* address, void* vector) {
	unsigned char xdata* tmp;

	*address = 0x02;

	tmp = (unsigned char xdata*)(address + 1);
	*tmp = (unsigned char)((unsigned short)vector >> 8);
	++tmp;

	*tmp = (unsigned char)vector;
}

void set_ena(uint8_t value) {
	write_max(ENA, value);
}

void time_handler() interrupt(3) {
	TH1 = time_delay[0];
	TL1 = time_delay[1];

	ms_count++;

	if(DTimeMs(last_note_swap) >= 10)
	{
		last_note_swap = GetMsCounter();
		compute_note_delay(++current_note);
		if(current_note == 6)
		{
			current_note = 0;
		}

		leds(current_note);
	}
}

void note_handler() interrupt(1) {
	TH0 = note_delay[1];
	TL0 = note_delay[0];

	current_ena = ~current_ena;
	set_ena(current_ena);
}

void InitTimer() {
    last_note_swap = 10;
	current_ena = 0x18;

	compute_note_delay(0);
	compute_timer_delay();
	ms_count = 0;

	TH0 = 0xFF;
	TL0 = 0xFF;

	TH1 = 0xFF;
	TH0 = 0xFF;

	TCON = 0x50;
	TMOD = 0x11;

	set_vector(0x200B, (void*)note_handler);
	set_vector(0x201B, (void*)time_handler);

	ET0 = 1;
	ET1 = 1;
	EA = 1;
}

unsigned long GetMsCounter(void) {
	return ms_count;
}

unsigned long DTimeMs(unsigned long t0) {
	return ms_count - t0;
}

void DelayMs(unsigned long t) {
	unsigned long target = t + ms_count;
	while(ms_count != target);
}

void compute_note_delay(uint8_t index) {
	uint16_t delay = 460791/notes[index];
	uint8_t* ptr = (uint8_t*)&delay;
	note_delay[0] = 0xFF - ptr[0];
	note_delay[1] = 0xFF - ptr[1];
}

void compute_timer_delay() {
	uint16_t delay = 921583/1000;
	uint8_t* ptr = (uint8_t*)(&delay);

	time_delay[0] =  0xFF - ptr[0];
	time_delay[1] =  0xFF - ptr[1];
}
