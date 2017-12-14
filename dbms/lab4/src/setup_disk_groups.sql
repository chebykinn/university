startup

CREATE DISKGROUP cutegoat EXTERNAL REDUNDANCY DISK
	'/u01/cutegoat/cutegoat0',
	'/u01/cutegoat/cutegoat1',
	'/u01/cutegoat/cutegoat2',
	'/u01/cutegoat/cutegoat3',
	'/u01/cutegoat/cutegoat4',
	'/u01/cutegoat/cutegoat5',
	'/u01/cutegoat/cutegoat6'
	ATTRIBUTE 'COMPATIBLE.ASM'='11.2.0.0.0';

CREATE DISKGROUP cleverdog EXTERNAL REDUNDANCY DISK
	'/u01/cleverdog/cleverdog0',
	'/u01/cleverdog/cleverdog1',
	'/u01/cleverdog/cleverdog2'
	ATTRIBUTE 'COMPATIBLE.ASM'='11.2.0.0.0';
