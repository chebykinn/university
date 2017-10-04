if (!exists("driver")) driver='pdf'
if (!exists("filename")) filename='img/linear-filter.png'
if (!exists("datasrc"))  datasrc='linear-filter.dat'
set style data lines
set xlabel "Количество импульсных помех"
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
plot datasrc using 1:2 linetype rgb "#2C3B63"

# vim: set ft=gnuplot:

