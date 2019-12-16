#define _DEFAULT_SOURCE
#include "ipc.h"

#include <assert.h>
#include <string.h>
#include <unistd.h>

#include "io.h"

static Channel *get_channel(IOHandle *handle, local_id from, local_id to) {
    assert(handle != NULL);
    if (from < 0 && from > handle->proc_num) return NULL;
    if (to < 0 && to > handle->proc_num) return NULL;
    return &handle->channel_table[from * handle->proc_num + to];
}

int send(void *self, local_id dst, const Message *msg) {
    assert(self != NULL);
    assert(msg != NULL);

    IOHandle *h = (IOHandle *)self;
    if (dst == h->src_pid) return 0;

    Channel *c = get_channel(h, h->src_pid, dst);
    if (c == NULL) return -1;

    size_t msg_size = sizeof msg->s_header + msg->s_header.s_payload_len;
    int rc = write(c->writefd, msg, msg_size);
    if (rc < 0 || rc != msg_size) return -1;

    return 0;
}

int send_multicast(void *self, const Message *msg) {
    assert(self != NULL);
    assert(msg != NULL);

    IOHandle *h = (IOHandle *)self;

    for (local_id pid = 0; pid < h->proc_num; pid++) {
        int rc = send(self, pid, msg);
        if (rc < 0) return -1;
    }
    return 0;
}

int receive(void *self, local_id from, Message *msg) {
    assert(self != NULL);
    assert(msg != NULL);

    IOHandle *h = (IOHandle *)self;
    if (from == h->src_pid) return 0;

    Channel *c = get_channel(h, from, h->src_pid);
    if (c == NULL) return -1;

    while (1) {
        int rc = read(c->readfd, msg, sizeof msg->s_header);
        if (rc <= 0) {
            goto sleep;
        }
        rc = read(c->readfd, msg->s_payload, msg->s_header.s_payload_len);
        if (rc >= 0) {
            return 0;
        }
    sleep:
        usleep(1000);
    }
}

int receive_any(void *self, Message *msg) {
    assert(self != NULL);
    assert(msg != NULL);

    IOHandle *h = (IOHandle *)self;
    while (1) {
        for (local_id pid = 0; pid < h->proc_num; pid++) {
            if (pid == h->src_pid) continue;

            Channel *c = get_channel(h, pid, h->src_pid);
            if (c == NULL) return -1;

            int rc = read(c->readfd, msg, sizeof msg->s_header);
            if (rc <= 0) {
                continue;
            }
            rc = read(c->readfd, msg->s_payload, msg->s_header.s_payload_len);
            if (rc >= 0) {
                h->last_msg_pid = pid;
                return 0;
            }
        }
        usleep(1000);
    }
    return 0;
}
