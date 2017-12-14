TEXINPUTS=../
INCLUDE_DIR=../../libreport
SRCDIR=.

include $(INCLUDE_DIR)/Makefile

all: stdev_u.pdf stdev_f.pdf $(HEADERS_OUT) $(TEMPLATES_OUT) $(DOCS)
	@rm -f $(TEMPLATES_OUT)
	@rm -f $(HEADERS_OUT)

%.pdf: %.dat stdev.gnu
	gnuplot -e 'datasrc="$<"' -e 'filename="$@"' stdev.gnu

stdev_%.dat: src/fold.rs src/main.rs src/fold_f32.rs
	cargo run

clean:
	cargo clean
	rm -f stdev_*.dat report.pdf report.aux report.log stdev_*.pdf

.PHONY: all clean
