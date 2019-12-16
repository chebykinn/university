#ifndef _LABDEFS_H_
#define _LABDEFS_H_

#include "banking.h"
#include "pa2345.h"

void move_time();
void set_max_time(const timestamp_t* new_time);

typedef struct {
    local_id        s_id;
    uint8_t         s_history_len;
    BalanceState    s_history[MAX_T + 1]; ///< Must be used as a buffer, unused
                                          ///< part of array shouldn't be transfered
} __attribute__((packed)) BalanceHistory;

typedef struct timevector_t {
    timestamp_t vec[MAX_PROCESS_ID];
} timevector_t;

timevector_t get_vector_timevec();
#endif
