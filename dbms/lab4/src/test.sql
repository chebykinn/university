cat << 'EOF' | sqlplus / as sysasm 
set pagesize 300
column name format a20
column path format a35
select name, path, mount_status from v$asm_disk order by path;
EOF

