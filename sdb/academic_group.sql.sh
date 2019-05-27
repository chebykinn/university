#!/bin/bash

sql="INSERT ALL\n"

for i in $(seq 2000 2019); do
    for j in $(seq 1 6); do
        year_no="$j"
        year_begin_date="$i-09-01"
        year_end_date="$((i + 1))-08-31"
        sql="$sql    INTO ACADEMIC_GROUP (ID,YEAR_NO,YEAR_BEGIN_DATE,YEAR_END_DATE)"
        sql="$sql VALUES(NULL, $year_no, TO_DATE('$year_begin_date', 'yyyy-mm-dd'), TO_DATE('$year_end_date', 'yyyy-mm-dd'))\n"
    done
done
sql="${sql}SELECT 1 FROM DUAL;"
echo -e "$sql"
