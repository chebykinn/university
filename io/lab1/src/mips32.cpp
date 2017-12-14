#include "mips32.h"
#include "Timer.h"
#include "BusMatrix.h"
#include "input_capture/ICCONF.h"
#include "input_capture/FIFO.h"

using namespace std;

MIPS32::MIPS32(sc_module_name nm)
    :sc_module(nm),
    clk_i("clk_i"),
    addr_bo("addr_bo"),
    data_bi("data_bi"),
    data_bo("data_bo"),
    wr_o("wr_o"),
    rd_o("rd_o")
{
    addr_bo.initialize(0);
    data_bo.initialize(0);
    wr_o.initialize(0);
    rd_o.initialize(0);

    mips_state = 0;
    mips_inner_state = 0;

    SC_CTHREAD(mainThread, clk_i.pos());
    
}

void MIPS32::mainThread() {
    const sc_uint<32> ic_wait_for_pos_conf = ICCONF::build_conf(ICCONF::ICStoreAtPos, ICCONF::ICTimersFirst);
    const sc_uint<32> ic_wait_for_any_conf = ICCONF::build_conf(ICCONF::ICStoreAtAny, ICCONF::ICTimersFirst);

    sc_uint<32> pos_time = 0;
    sc_uint<32> neg_time = 0;

    while (true) {
        switch (mips_state) {
            case 0: { // init
                bus_write(FIRST_TIMER_OFFSET + Timer::Tmr, 0xffffffff);
                bus_write(FIRST_TIMER_OFFSET + Timer::Tconf, Timer::Inc);
                mips_state = 1;
                break;
            }
            case 1: { // configure wait for pos
                bus_write(FIFO::ICCONF, ic_wait_for_pos_conf);
                mips_state = 3;
                mips_inner_state = 0;
                break;
            }
            case 2: { // configure wait for any
                bus_write(FIFO::ICCONF, ic_wait_for_any_conf);
                mips_state = 4;
                mips_inner_state = 1;
                break;
            }
            case 3: { // wait for NOT EMPTY
                auto icconf = bus_read(FIFO::ICCONF);
                if (ICCONF::get_icbne(icconf) == ICCONF::FIFONotEmpty) {
                    switch (mips_inner_state) {
                        case 0: {
                            mips_state = 2;
                            break;
                        }
                        case 1: {
                            mips_state = 4;
                            break;
                        }
                        case 2: {
                            mips_state = 5;
                            break;
                        }
                    }
                }

                if (ICCONF::get_icov(icconf) == ICCONF::FIFOFull) {
                    cout << "Error: FIFO overflow\n";
                    abort();
                }
                break;
            }
            case 4: { // read posedge time
                pos_time = bus_read(FIFO::ICBUF);
                mips_inner_state = 2;
                mips_state = 3;
                break;
            }
            case 5: { // calculate duration
                neg_time = bus_read(FIFO::ICBUF);
                auto duration = neg_time - pos_time;
                if (neg_time < pos_time) {
                    cout << "timer is overflow\n";
                } else {
                    cout << "time: " << sc_time_stamp() << ", duration: " << duration << "\n";
                }
                mips_inner_state = 1;
                mips_state = 3;
                break;
            }
        }
    }

}

sc_uint<32> MIPS32::bus_read(sc_uint<32> addr) {
    sc_uint<32> data;

    wait();
    addr_bo.write(addr);
    rd_o.write(1);
    
    wait();
    rd_o.write(0);
    
    wait();
    data = data_bi.read();
    
    return data;
}

void MIPS32::bus_write(sc_uint<32> addr, sc_uint<32> data) {
    wait();
    addr_bo.write(addr);
    data_bo.write(data);
    wr_o.write(1);
    
    wait();
    wr_o.write(0);
}
