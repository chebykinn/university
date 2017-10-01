if (!exists("driver")) driver='pdf'
if (!exists("filename")) filename='by-amp-ratio.png'
if (!exists("datasrc"))  datasrc='by-amp-ratio.dat'
set style data lines
set xlabel "An/As"
set ylabel "SNR out"
set key autotitle columnhead
set grid ytics mytics
set mytics 5
set terminal dumb
set output '/dev/null'
set xrange [50: 250]
# set xtics 0,0.5,3.0
plot for [IDX=0:3] datasrc i IDX u 2:3
MAX=(1 + floor(GPVAL_Y_MAX * 0.12)) * 10
set ytics 0,(MAX / 5),MAX
set yrange [0 : MAX] noreverse nowriteback
set terminal png size 400,300 enhanced
set output filename
plot for [IDX=0:3] datasrc i IDX u 2:3

# vim: set ft=gnuplot:

