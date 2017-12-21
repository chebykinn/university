#!/bin/sh

sqlplusdb <<EOF
CREATE TABLE testing (id int, str varchar2(255));
INSERT INTO TESTING VALUES (1, 'test');
INSERT INTO TESTING VALUES (2, 'line');
INSERT INTO TESTING VALUES (3, 'another');
COMMIT;

EOF
