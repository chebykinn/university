# Ultimate Makefile v1.0.1 (C) 2016 Ivan Chebykin
# NOTE: GNU Make only

# Variables--------------------------------------------------------------------
PROJECT=text.rom

PREFIX=mips-sde-elf
CPREFIX=$(PREFIX)-
CPATH=./compiler/bin

CC=$(CPATH)/$(CPREFIX)as
LD=$(CPATH)/$(CPREFIX)ld
OBJDUMP=$(CPATH)/$(CPREFIX)objdump
OBJCOPY=$(CPATH)/$(CPREFIX)objcopy
CONVERT=python convert.py 512 1024

CFLAGS=
LDFLAGS=

OBJEXT=o
SRCEXT=asm

SRCDIR=src/sw
BUILDDIR=build
OBJDIR=obj

OBJPATH=$(BUILDDIR)/$(OBJDIR)
TARGET=$(BUILDDIR)/$(PROJECT)

SOURCES=$(shell find $(SRCDIR) -type f -name "*.$(SRCEXT)")
OBJECTS=$(patsubst $(SRCDIR)/%,$(OBJPATH)/%,$(SOURCES:.$(SRCEXT)=.$(OBJEXT)))

# Targets----------------------------------------------------------------------
all: $(TARGET)

$(TARGET): $(OBJECTS)
	@mkdir -p $(BUILDDIR)/$(OBJDIR)
	$(CONVERT)

$(OBJPATH)/%.$(OBJEXT): $(SRCDIR)/%.$(SRCEXT)
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $< -o $@
	$(LD) -Ttext 0x0 -Tdata 0x200 -eentry -o $@.axf $@
	$(OBJDUMP) --disassemble $@.axf > $@.lst
	$(OBJCOPY) -O verilog -R .text $@.axf $(dir $@)data.vh
	$(OBJCOPY) -O verilog -R .data $@.axf $(dir $@)text.vh

clean:
	rm -rf $(BUILDDIR)

cleanobj:
	rm -rf $(OBJPATH)

remake: clean all

.PHONY: all clean cleanobj remake
