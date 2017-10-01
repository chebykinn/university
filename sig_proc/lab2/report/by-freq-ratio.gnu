if (!exists("driver")) driver='pdf'
if (!exists("filename")) filename='by-freq-ratio.png'
if (!exists("datasrc"))  datasrc='by-freq-ratio.dat'
set style data lines
set xlabel "Fs/Fn"
set ylabel "SNR out"
set nokey
set grid ytics mytics
set mytics 5
set terminal dumb
set output '/dev/null'
plot datasrc
MAX=(1 + floor(GPVAL_Y_MAX * 0.12)) * 10
set ytics 0,(MAX / 5),MAX
set yrange [0 : MAX] noreverse nowriteback
set terminal png size 400,300 enhanced
set output filename
plot datasrc using 2:3 linetype rgb "#2C3B63"

# vim: set ft=gnuplot:

