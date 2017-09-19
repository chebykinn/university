# ---------------------------------------------------------------------------
# Имя проекта

NAME	= lab1

# Настройки компилятора и линкера

CC      = sdcc
CFLAGS  = -I./include -c --stack-auto
LFLAGS  = --code-loc 0x2100 --xram-loc 0x6000 --stack-auto --stack-loc 0x80

# Настройки системы автоинкремента версии сборки

PROJECT  = $(NAME)
VERSION  = 0.0.1

PROJNAME = ${PROJECT}-${VERSION}
TARBALL  = ${PROJNAME}.tar

# Настройки M3P

M3P		 = m3p
COMPORT	 = com1
COMLOG	 = $(COMPORT)_log.txt
BAUD	 = 9600

# Каталоги с исходными текстами

SRC_DIR = src
BUILD_DIR = target

TARGET = $(BUILD_DIR)/$(PROJECT)
# ---------------------------------------------------------------------------

all: $(TARGET)

clean:
	@rm -rf $(BUILD_DIR)
	@rm -f pm3p_*.txt
	@rm -f com?_log.txt
	@rm -f $(TARBALL).gz
	@rm -f $(SRC_DIR)/*.asm
	@rm -f $(SRC_DIR)/*.rel
	@rm -f $(SRC_DIR)/*.rst
	@rm -f $(SRC_DIR)/*.sym
	@rm -f $(SRC_DIR)/*.lst

load:
	$(M3P) lfile load.m3p


dist:
	tar -cvf $(TARBALL) --exclude=*.tar .
	gzip $(TARBALL)

term:
	$(M3P) echo $(COMLOG) $(BAUD)  openchannel $(COMPORT) +echo 6 term -echo bye



LIST_SRC = \
$(SRC_DIR)/led.c \
$(SRC_DIR)/max.c \
$(SRC_DIR)/lab.c

LIST_OBJ = $(LIST_SRC:.c=.rel)

$(TARGET) : $(LIST_OBJ) Makefile
	@mkdir -p $(BUILD_DIR)
	$(CC) $(LIST_OBJ) -o $(TARGET).hex $(LFLAGS)
	$(M3P) hb166 $(TARGET).hex $(TARGET).bin bye


$(LIST_OBJ) : %.rel : %.c Makefile
	$(CC) -c $(CFLAGS) $< -o $@

