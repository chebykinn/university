if (!exists("driver")) driver='pdf'
if (!exists("filename")) filename='stdev.pdf'
if (!exists("datasrc"))  datasrc='stdev.dat'
set style data lines
set xlabel "Vector length"
set ylabel "St. dev"
set key autotitle columnhead
set grid ytics mytics
set mytics 5
set terminal dumb
set output '/dev/null'
plot for [IDX=0:6] datasrc i IDX u 1:2
MAX=(1 + floor(GPVAL_Y_MAX * 0.12)) * 10
set ytics -MAX,(MAX / 5),MAX
set yrange [-1 : MAX] noreverse nowriteback
set terminal driver
set output filename
plot for [IDX=0:6] datasrc i IDX u 1:2

# vim: set ft=gnuplot:

