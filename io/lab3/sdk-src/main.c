#include <stdio.h>
#include "platform.h"
#include "xil_printf.h"
#include <xil_io.h>
#include "mb_interface.h"
//#include "xintc.h"

#define BRAM_ADDRESS 		0xC0000000
#define GPIO_ADDRESS 		0x40000000
#define AXI_TIMER_0_ADDRESS	0x41C00000
#define AXI_TIMER_1_ADDRESS	0x41C10000

#define TIMER1_OFFSET	0x10

#define TCS_OFFSET 	0x0
#define TL_OFFSET 	0x4
#define TC_OFFSET 	0x8

#define INTERRUPT	0x100

#define ENALL 	0x400 //enable all timers
#define PWMA	0x200 //enable pulse width modulation
#define ENT		0x080 //enable timer
#define ENIT	0x040 //enable interrupt
#define ARHT	0x010 //auto reload timer
#define GENT	0x004 //enable external general signal
#define DTM		0x002 //down timer mode

#define CYCLE_TIME 1000
#define LOW_TIME 60

enum cycle_t {
	FIRST = 0,
	SECOND = 1,
	THIRD = 2
};


#define BRAM_TIMER_0_OFFSET 0


#define STORE_AT_POS 0x3
#define STORE_AT_ANY 0x1

#define IC_TIMERS_FIRST 0x1

#define TMR 0
#define TVAL 4
#define TCONF 8

#define ICCONF 0x18
#define ICBUF 0x1C

#define INC 0x2

#define FIFO_NOT_EMPTY 0x8
#define FIFO_FULL 0x10

volatile static u8 cycle = FIRST;
volatile static u32 high_time[] = {40, 60, 80};
void int_handler(void) __attribute__ ((interrupt_handler));
void my_reverse(char str[], int len);
char* my_itoa(int num, char* str, int base);

/*
* function to reverse a string
*/
void my_reverse(char str[], int len)
{
    int start, end;
    char temp;
    for(start=0, end=len-1; start < end; start++, end--) {
        temp = *(str+start);
        *(str+start) = *(str+end);
        *(str+end) = temp;
    }
}

char* my_itoa(int num, char* str, int base)
{
    int i = 0;
    int isNegative = 0;

    /* A zero is same "0" string in all base */
    if (num == 0) {
        str[i] = '0';
        str[i + 1] = '\0';
        return str;
    }

    /* negative numbers are only handled if base is 10
       otherwise considered unsigned number */
    if (num < 0 && base == 10) {
        isNegative = 1;
        num = -num;
    }

    while (num != 0) {
        int rem = num % base;
        str[i++] = (rem > 9)? (rem-10) + 'A' : rem + '0';
        num = num/base;
    }

    /* Append negative sign for negative numbers */
    if (isNegative){
        str[i++] = '-';
    }

    str[i] = '\0';

    my_reverse(str, i);

    return str;
}



void int_handler(void) {
	Xil_Out32(AXI_TIMER_1_ADDRESS + TCS_OFFSET, INTERRUPT | ENT | ENIT | ARHT | DTM);
	if (cycle == THIRD) {
		cycle = FIRST;
	} else {
		cycle++;
	}
	Xil_Out32(AXI_TIMER_0_ADDRESS + TL_OFFSET, LOW_TIME + high_time[cycle] - 2);
	Xil_Out32(AXI_TIMER_0_ADDRESS + TIMER1_OFFSET + TL_OFFSET, high_time[cycle] - 2);

	Xil_Out32(GPIO_ADDRESS, high_time[cycle] * 10);
}

static u32 build_conf(u32 icm, u32 ictmr) {
    return icm | (ictmr << 5);
}

int main() {
    init_platform();
	const u32 ic_wait_for_any_conf = build_conf(STORE_AT_ANY, IC_TIMERS_FIRST);

	u32 pos_time = 0;
	u32 neg_time = 0;

	u8 mips_state = 0;
	u8 mips_inner_state = 0;
	char buff[32];

	while (1) {
		switch (mips_state) {
			case 0: { // init
				Xil_Out32(BRAM_ADDRESS + BRAM_TIMER_0_OFFSET + TMR, 0xffffffff);
				Xil_Out32(BRAM_ADDRESS + BRAM_TIMER_0_OFFSET + TCONF, INC);
				Xil_Out32(BRAM_ADDRESS + ICCONF, ic_wait_for_any_conf);

				Xil_Out32(AXI_TIMER_0_ADDRESS + TL_OFFSET, LOW_TIME + high_time[cycle] - 2);
				Xil_Out32(AXI_TIMER_0_ADDRESS + TIMER1_OFFSET + TL_OFFSET, high_time[cycle] - 2);
				Xil_Out32(AXI_TIMER_1_ADDRESS + TL_OFFSET, CYCLE_TIME - 2);

				Xil_Out32(AXI_TIMER_0_ADDRESS + TCS_OFFSET, PWMA | GENT | DTM);
				Xil_Out32(AXI_TIMER_0_ADDRESS + TIMER1_OFFSET + TCS_OFFSET, ENALL | PWMA | GENT | DTM);
				Xil_Out32(AXI_TIMER_1_ADDRESS + TCS_OFFSET, ENT | ARHT | DTM);

				Xil_Out32(AXI_TIMER_1_ADDRESS + TCS_OFFSET, INTERRUPT | ENT | ENIT | ARHT | DTM);
				Xil_Out32(GPIO_ADDRESS, high_time[cycle] * 10);

				microblaze_enable_interrupts();

				mips_state = 1;
				break;
			}
			case 1: { // wait for NOT EMPTY
				u32 icconf = Xil_In32(BRAM_ADDRESS + ICCONF);
				if (icconf & FIFO_NOT_EMPTY) {
					switch (mips_inner_state) {
						case 0: {
							mips_state = 2;
							break;
						}
						case 1: {
							mips_state = 3;
							break;
						}
					}
				}

				if (icconf == FIFO_FULL) {
					print("Error: FIFO overflow\n");
				}
				break;
			}
			case 2: { // read posedge time
				pos_time = Xil_In32(BRAM_ADDRESS + ICBUF);
				mips_inner_state = 1;
				mips_state = 1;
				break;
			}
			case 3: { // calculate duration
				neg_time = Xil_In32(BRAM_ADDRESS + ICBUF);
				s32 duration = neg_time - pos_time;
				if (neg_time < pos_time) {
					print("timer is overflow\n");
				} else {
					char *out = my_itoa(duration, buff, 10);
					print(out);
					//print("duration:" ); print(duration);

				}
				mips_inner_state = 0;
				mips_state = 1;
				break;
			}
		}
	}
}

