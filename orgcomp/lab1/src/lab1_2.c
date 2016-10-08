char from = 'a';
char to = 'A';

// source char array in the ROM.
char code source[] = { "This programmator" }; 

// pointer in DATA to 0x000000 in XDATA.
char xdata *target;

int main() {
    unsigned char i;
    for( i = 0; source[i] != 0; i++ ) { 
		
        if( source[i] == from )
        	target[i] = to;
		else
			target[i] = source[i];
			
    }

    while(1);   // halt
}