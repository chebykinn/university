#include <reg51.h>

int count = 0, min = 0, sec = 0, Ts = 65000, Tm = 60, Too = 1;

intt0() interrupt 1 {
	TH0= -(Too >>8);
	TL0= -Too;
	count++;
	if(count== Ts){
		sec++;count=0;
	}
	if(sec== Tm ){
		min++; sec=0;
	}
	P1=sec;
	P2=min;
}
main() {
	TMOD=1; //режим TM0 – 16 разрядный счетчик
	ET0=1; //маска TM0
	TR0=1; //разрешение счета
	EA=1;
	while(1);
}
