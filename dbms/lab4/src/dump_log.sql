shutdown immediate;
startup nomount pfile=inits191954_rep.ora
alter database mount;
recover standby database until cancel;
auto
alter database open;
exit;
