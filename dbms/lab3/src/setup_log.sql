startup mount;
alter database archivelog;
alter system archive log start;
alter database create standby controlfile as '/u01/replica/loudoven/ctrl_rep1.ctl';
shutdown immediate;
