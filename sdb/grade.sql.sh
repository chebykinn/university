#!/bin/sh



## utility
# end dates
mydate=$(which date)
start_dt="2000-01-01"
end_dt="2019-01-01"
start_sec=$($mydate -d "$start_dt" +%s)
end_sec=$($mydate -d "$end_dt" +%s)
#echo "$startSec"
#echo "$endSec"
let "diff_sec = end_sec - start_sec"
#echo "$diffSec"

function rnd()
{
	rnd=${RANDOM}${RANDOM}
	let diff=rnd%diff_sec
	let rnd=diff+start_sec
	echo $rnd
}


sql="INSERT ALL\n"
ids_cnt="$(wc -l person_ids.csv | cut -d' ' -f1)"
data="$(cat person_ids.csv)"
for i in `seq 1 1000`; do
    #dt="$(date -d @$ts --iso-8601)"
    dt="$(date -d @$(rnd) --iso-8601)"
    #echo $dt
    #exit
    letter="$(cat /dev/urandom | tr -dc 'A-E' | fold -w 256 | head -n 1 | head --bytes 1)"
    student="$((1 + RANDOM % $ids_cnt))"
    grade="$((2 + RANDOM % 5))"
    id="$(echo "$data" | awk 'NR=='$student'{print $0}')"
    sql="$sql    INTO GRADE (GRADE_ID,STUDENT_ID,GRADE,LETTER,DATE)"
    sql="$sql VALUES(NULL, $id, $grade, '$letter', TO_DATE('$dt', 'yyyy-mm-dd'))\n"
done
sql="${sql}SELECT 1 FROM DUAL;"
echo -e "$sql"
