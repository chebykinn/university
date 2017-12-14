startup nomount pfile=inits191954_rep.ora
alter database mount;
recover standby database;
alter database open;
