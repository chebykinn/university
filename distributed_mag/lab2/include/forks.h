#pragma once

#include "labdefs.h"

typedef struct ForkData {
    int clean[MAX_PROCESS_ID + 1];
    int dirty[MAX_PROCESS_ID + 1];
    int reqf[MAX_PROCESS_ID + 1];
} ForkData;

extern ForkData* fork_data;

void fork_init(IOHandle *handle, ForkData **fork);
int fork_check_forks(IOHandle *handle, ForkData *fork);
int fork_set_all_dirty(IOHandle *handle, ForkData *fork);
