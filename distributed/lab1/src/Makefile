# Ultimate Makefile v2.0.4 (C) 2017 Ivan Chebykin
# NOTE: GNU Make only

# Configuration
PROJECT=lab1
VERSION=v0.0.1

# Any non-empty value (e.g. DEBUG=true) enables debug builds.
DEBUG=true

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
TARGET_CFLAGS=-pedantic
TARGET_LDFLAGS=

TEST_CFLAGS=
TEST_LDFLAGS=

DEBUG_CFLAGS=
DEBUG_LDFLAGS=

RELEASE_CFLAGS=
RELEASE_LDFLAGS=

EXCLUDE_FILES=
TEST_EXCLUDE_FILES=

# Directories------------------------------------------------------------------
SRCDIR=.
OBJDIR=obj
TESTDIR=tests
BUILDROOT=target
MKINCLUDEDIR=mk

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

# Documentation configuration
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

INSTALL_INCLUDES?=$(firstword $(INCLUDEDIRS))

# Include actual Makefile
include $(MKINCLUDEDIR)/Makefile.include
