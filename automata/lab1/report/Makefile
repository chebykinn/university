BASEDIR=..
OBJPATH=$(BASEDIR)/img
SRCDIR=$(BASEDIR)/src
SRCEXT=gv
OBJEXT=png
INPUT_TEX=report.tex

SOURCES=$(shell find $(SRCDIR) -type f -name "*.$(SRCEXT)")
OBJECTS=$(patsubst $(SRCDIR)/%,$(OBJPATH)/%,$(SOURCES:.$(SRCEXT)=.$(OBJEXT)))

all: graphs
	TEXINPUTS=$(TEXINPUTS):../../../assets/:../ xelatex report.tex

graphs: $(OBJECTS)

$(OBJPATH)/%.$(OBJEXT): $(SRCDIR)/%.$(SRCEXT)
	dot -T$(OBJEXT) $< -o $@
