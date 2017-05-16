#ifndef _UTIL_H_
#define _UTIL_H_

#include "io.h"
#include "ipc.h"

void create_message(Message *msg, MessageType type, size_t length);

int log_event(char *msg);
int get_proc_num_from_args(int argc, char *const argv[]);
int create_handle(int proc_num, IOHandle *handle);
int create_pipes(IOHandle *handle);
int spawn_childs(IOHandle *handle);

int parent(IOHandle *handle);
int child(IOHandle *handle, void *data);

extern int parent_work(IOHandle *handle);
extern int parent_atexit(IOHandle *handle);

extern int child_work(IOHandle *handle, void *data);
extern int child_atexit(IOHandle *handle, void *data);

extern void child_set_started_msg(char *msg, IOHandle *handle, void *data);
extern void child_set_received_all_started_msg(char *msg, IOHandle *handle);
extern void child_set_done_msg(char *msg, IOHandle *handle, void *data);
extern void child_set_received_all_done_msg(char *msg, IOHandle *handle);

#endif
