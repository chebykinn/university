if (!exists("driver")) driver='pdf'
if (!exists("filename")) filename='img/by-num-noise.png'
if (!exists("datasrc"))  datasrc='by-num-noise.dat'
set style data lines
set xlabel "Количество импульсных помех"
set ylabel "SNR out"
set key autotitle columnhead
set grid ytics mytics
set mytics 5
set terminal dumb
set output '/dev/null'
plot for [IDX=0:4] datasrc i IDX u 1:2
MAX=(1 + floor(GPVAL_Y_MAX * 0.12)) * 10
set ytics 0,(MAX / 5),MAX
set yrange [0 : MAX] noreverse nowriteback
set terminal png size 400,300 enhanced
set output filename
plot for [IDX=0:4] datasrc i IDX u 1:2

# vim: set ft=gnuplot:

