# Ultimate Makefile v2.0.6 (C) 2017 Ivan Chebykin
# NOTE: GNU Make only

# Variables--------------------------------------------------------------------
ARCH=
PROJECT=pa
VERSION=0.0.1
DEBUG=
#CC=clang

# TODO: examples
# Supported: project, test
BUILD_TYPE=project
# Supported: bin, dylib
PROJECT_TYPE=bin
# Supported: C++, C
PROJECT_LANGUAGE=C

LIBS=
TEST_LIBS=
DEBUG_LIBS=
RELEASE_LIBS=

# Flags
CFLAGS=-std=c99 -pedantic
PROJECT_LDFLAGS=

TEST_CFLAGS=
TEST_LDFLAGS=

DEBUG_CFLAGS=-fsanitize=address
DEBUG_LDFLAGS=

RELEASE_CFLAGS=
RELEASE_LDFLAGS=

EXCLUDE_FILES=
TEST_EXCLUDE_FILES=

# Directories------------------------------------------------------------------
SRCDIR=src
OBJDIR=obj
TESTDIR=tests
BUILDROOT=target
# Directory for build artifacts
BUILDARTDIR=release
ifdef DEBUG
    BUILDARTDIR=debug
endif
MKINCLUDEDIR=mk
TARGETSDIR=targets
LANGUAGESDIR=languages
EXTENSIONSDIR=extensions

INCLUDEDIRS:=$(SRCDIR)/include
LIBDIRS=
TEST_LIBDIRS=
DEBUG_LIBDIRS=
RELEASE_LIBDIRS=

# Tools------------------------------------------------------------------------
# Remote host configuration
SCP=scp
SCPFLAGS=
REMOTE_HOST=
REMOTE_PATH=

# GDB configuration
GDB=gdb
GDBARGS=

# Formatter configuration
FMT=clang-format
FMTFLAGS=-i

DOC=doxygen
DOCFLAGS=Doxyfile

ENABLE_EXTENSIONS=true

# Install environment
INSTALL=install
CP=cp
DESTDIR=
BINDIR=/usr/bin
LIBDIR=/usr/lib
INCLUDEDIR=/usr/include
SYSCONF=/etc

INSTALL_INCLUDES=$(firstword $(INCLUDEDIRS))

-include Makefile.config
# Internal variables-----------------------------------------------------------
# Space char for subst
SPACE=
SPACE+=

HOST_ARCH=$(shell uname -m)
ifeq ($(ARCH),)
    ARCH=$(HOST_ARCH)
endif

# Check target
BUILD_TARGET=native
ifneq ($(ARCH),$(HOST_ARCH))
    BUILD_TARGET=cross
endif

# Target-specific variables
PREFIX?=
TOOLCHAIN_PATH?=
BIN_PATH?=

OUTNAME_bin=$(PROJECT)
OUTNAME_dylib=lib$(OUTNAME_bin).$(LIBEXT)

OUTNAME=$(OUTNAME_$(PROJECT_TYPE))

# Select build directory for currect arch
ifneq ($(BUILD_TARGET),native)
    OUT_BUILDROOT=$(BUILDROOT)/$(ARCH)
else
    OUT_BUILDROOT=$(BUILDROOT)
endif
BUILDDIR=$(OUT_BUILDROOT)/$(BUILDARTDIR)

OBJPATH=$(BUILDDIR)/$(OBJDIR)
TARGET=$(BUILDDIR)/$(OUTNAME)

# Check if building tests
ifeq ($(BUILD_TYPE),test)
    ifneq ($(TEST_LIBDIRS),)
        LIBDIRS+=$(TEST_LIBDIRS)
    endif

    ifneq ($(TEST_LIBS),)
        LIBS+=$(TEST_LIBS)
    endif

    ifeq ($(PROJECT_TYPE), dylib)
        STATIC_BUILDDIR:=$(BUILDDIR)
        LIBDIRS+=$(STATIC_BUILDDIR)
        LIBS+=$(PROJECT)
    endif
    SRCDIR=$(TESTDIR)
    PROJECT_CFLAGS=$(TEST_CFLAGS)
    PROJECT_LDFLAGS=$(TEST_LDFLAGS)
    EXCLUDE_FILES=$(TEST_EXCLUDE_FILES)
    BUILDDIR:=$(BUILDDIR)/test
    TARGET=$(BUILDDIR)/$(OUTNAME)-test
endif

ifeq ($(PROJECT_LANGUAGE),C++)
SRCEXT?=cpp
CXX?=g++
LANG_CC?=$(CXX)

ifdef CXXFLAGS
    CFLAGS=$(CXXFLAGS)
endif

ifeq ($(filter -std=%,$(CFLAGS)),)
    override CFLAGS+=-std=c++14
endif


endif

LIBEXT?=so
OBJEXT?=o
SRCEXT?=c
INCEXT?=h
ifeq ($(CC),cc)
CC=gcc
endif
LANG_CC?=$(CC)
LDFLAGS?=

ifeq ($(filter -Wall,$(CFLAGS)),)
    override CFLAGS+=-Wall
endif
ifeq ($(filter -std=%,$(CFLAGS)),)
    override CFLAGS+=-std=c11
endif

ifeq ($(PROJECT_TYPE),dylib)
ifneq ($(BUILD_TYPE),test)
    ifeq ($(filter -fPIC,$(CFLAGS)),)
        override CFLAGS+=-fPIC
    endif
    ifeq ($(filter -shared,$(LDFLAGS)),)
        override LDFLAGS+=-shared
    endif
endif
endif

ifeq ($(filter -O%,$(CFLAGS)),)
    RELEASE_CFLAGS+=-O3
    DEBUG_CFLAGS+=-O0
endif

ifeq ($(filter -g,$(CFLAGS)),)
    DEBUG_CFLAGS+=-g
endif
DEBUG_CFLAGS+=-D_DEBUG

ifeq ($(filter -D_VERSION=%,$(LDFLAGS)),)
    override CFLAGS+=-D_VERSION=$(VERSION)
endif

ifdef DEBUG
    LIBDIRS+=$(DEBUG_LIBDIRS)
    LIBS+=$(DEBUG_LIBS)
else
    LIBDIRS+=$(RELEASE_LIBDIRS)
    LIBS+=$(RELEASE_LIBS)
endif

INCLUDEDIRS:=$(strip $(INCLUDEDIRS))
LIBDIRS:=$(strip $(LIBDIRS))
LIBS:=$(strip $(LIBS))

OUT_INCLUDEDIRS?=
ifneq ($(INCLUDEDIRS),)
    OUT_INCLUDEDIRS=-I$(subst $(SPACE),$(SPACE)-I,$(INCLUDEDIRS))
endif

OUT_LIBDIRS?=
ifneq ($(LIBDIRS),)
    OUT_LIBDIRS=-L$(subst $(SPACE),$(SPACE)-L,$(LIBDIRS))
endif

OUT_LIBS?=
ifneq ($(LIBS),)
    OUT_LIBS=-l$(subst $(SPACE),$(SPACE)-l,$(LIBS))
endif

OUT_INCLUDEDIRS:=$(strip $(OUT_INCLUDEDIRS))
OUT_LIBDIRS:=$(strip $(OUT_LIBDIRS))
OUT_LIBS:=$(strip $(OUT_LIBS))

override CFLAGS +=$(OUT_INCLUDEDIRS)
override LDFLAGS+=$(OUT_LIBDIRS)
override LDFLAGS+=$(OUT_LIBS)

override CFLAGS:=$(strip $(CFLAGS))
override LDFLAGS:=$(strip $(LDFLAGS))


# Add trailing dash to prefix
OUT_PREFIX?=
ifneq ($(PREFIX),)
    OUT_PREFIX=$(PREFIX)-
endif
# Add trailing slash to compiler path
OUT_BIN_PATH?=
ifneq ($(BIN_PATH),)
    OUT_BIN_PATH=$(BIN_PATH)/
endif

override CC:=$(OUT_BIN_PATH)$(OUT_PREFIX)$(LANG_CC)

ifdef DEBUG
    override CFLAGS +=$(DEBUG_CFLAGS)
    override LDFLAGS+=$(DEBUG_LDFLAGS)
else
    override CFLAGS +=$(RELEASE_CFLAGS)
    override LDFLAGS+=$(RELEASE_LDFLAGS)
endif

override CFLAGS +=$(PROJECT_CFLAGS)
override LDFLAGS+=$(PROJECT_LDFLAGS)

# TODO: Support mixed languages
ifneq ($(EXCLUDE_FILES),)
    TMP_EXCLUDE=$(strip $(EXCLUDE_FILES))
    OUT_EXCLUDE=! -path $(SRCDIR)/$(subst $(SPACE),$(SPACE)! -path $(SRCDIR)/,$(EXCLUDE_FILES))
endif
HEADERS=$(shell find -L $(INCLUDEDIRS) -type f -name "*.$(INCEXT)" $(OUT_EXCLUDE))
SOURCES=$(shell find -L $(SRCDIR) -type f -name "*.$(SRCEXT)" $(OUT_EXCLUDE))
OBJECTS=$(patsubst $(SRCDIR)/%,$(OBJPATH)/%,$(SOURCES:.$(SRCEXT)=.$(OBJEXT)))

# Targets----------------------------------------------------------------------
all: $(TARGET)

$(PROJECT): $(TARGET)

$(TARGET): $(OBJECTS)
	@mkdir -p $(BUILDDIR)/$(OBJDIR)
	$(CC) $^ -o $@ $(CFLAGS) $(LDFLAGS)

$(OBJPATH)/%.$(OBJEXT): $(SRCDIR)/%.$(SRCEXT)
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(BUILDROOT)

cleanobj:
	rm -rf $(OBJPATH)

cleantest:
	rm -rf $(OUT_BUILDROOT)/test

remake: clean all

test: $(TARGET)
	LD_LIBRARY_PATH=$(BUILDDIR) $(MAKE) \
	BUILD_TYPE=test run

run: $(TARGET)
	LD_LIBRARY_PATH=$(strip $(LIBDIRS)) ./$(TARGET) $(ARGS)

debug: $(TARGET)
	LD_LIBRARY_PATH=$(strip $(LIBDIRS)) $(GDB) $(GDBARGS) --args ./$(TARGET) $(ARGS)

fmt:
	$(FMT) $(FMTFLAGS) $(SOURCES)
	$(FMT) $(FMTFLAGS) $(HEADERS)

doc: $(TARGET)
	$(DOC) $(DOCFLAGS)

ifneq ($(SCP),)
copy-remote: $(TARGET)
		$(SCP) $(SCPFLAGS) $(TARGET) $(REMOTE_HOST):$(REMOTE_PATH)
endif

version:
	@echo $(VERSION)

install: $(TARGET)
ifeq ($(PROJECT_TYPE),bin)
	$(INSTALL) -d $(DESTDIR)$(BINDIR)
	$(INSTALL) -m 0755 $(TARGET) $(DESTDIR)$(BINDIR)
endif
ifeq ($(PROJECT_TYPE),dylib)
	$(INSTALL) -d $(DESTDIR)$(LIBDIR)
    ifdef INSTALL_INCLUDES
	    $(INSTALL) -d $(DESTDIR)$(INCLUDEDIR)
	    @$(CP) -vr $(INSTALL_INCLUDES)/* $(DESTDIR)$(INCLUDEDIR)
    endif
	$(INSTALL) -m 0755 $(TARGET) $(DESTDIR)$(LIBDIR)
endif

.PHONY: all clean cleanobj remake run fmt version install
