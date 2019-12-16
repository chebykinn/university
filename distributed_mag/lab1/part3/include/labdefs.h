#ifndef _LABDEFS_H_
#define _LABDEFS_H_

#include "pa2345.h"

timestamp_t get_lamport_time();
void move_time();
void set_max_time(timestamp_t new_time);

#endif
