#include "BusMatrix.h"
#include "Timer.h"
#include "input_capture/FIFO.h"

BusMatrix::BusMatrix(sc_module_name nm)
        :sc_module(nm),
         clk_i("clkdd_i"),
         addr_bi("addr_bi"),
         rd_i("rd_i"),
         wr_i("wr_i"),
         data_bo("data_bo"),

         timer1_data_bi("timer1_data_bi"),
         timer1_rd_o("timer1_rd_o"),
         timer1_wr_o("timer1_wr_o"),
         timer2_data_bi("timer2_data_bi"),
         timer2_rd_o("timer2_rd_o"),
         timer2_wr_o("timer2_wr_o"),
         input_capture_data_bi("input_capture_data_bi"),
         input_capture_rd_o("input_capture_rd_o"),
         input_capture_wr_o("input_capture_wr_o")
{
	slaves_data_bi.emplace_back(timer1_data_bi);
	slaves_data_bi.emplace_back(timer2_data_bi);
	slaves_data_bi.emplace_back(input_capture_data_bi);

	slaves_rd_o.emplace_back(timer1_rd_o);
	slaves_rd_o.emplace_back(timer2_rd_o);
	slaves_rd_o.emplace_back(input_capture_rd_o);

	slaves_wr_o.emplace_back(timer1_wr_o);
	slaves_wr_o.emplace_back(timer2_wr_o);
    slaves_wr_o.emplace_back(input_capture_wr_o);

    data_bo.initialize(0);

    for (size_t i = 0; i < NUMBER_OF_SLAVES; i++) {
        slaves_rd_o[i].get().initialize(0);
        slaves_wr_o[i].get().initialize(0);
    }

    SC_METHOD(on_change);
    sensitive << rd_i.value_changed() << wr_i.value_changed();
}

void BusMatrix::on_change() {
    switch (addr_bi.read()) {
        case FIRST_TIMER_OFFSET + Timer::Tmr:
        case FIRST_TIMER_OFFSET + Timer::Tval:
        case FIRST_TIMER_OFFSET + Timer::Tconf: {
            send(0);
            break;
        }
        case SECOND_TIMER_OFFSET + Timer::Tmr:
        case SECOND_TIMER_OFFSET + Timer::Tval:
        case SECOND_TIMER_OFFSET + Timer::Tconf: {
            send(1);
            break;
        }
        case FIFO::ICCONF:
        case FIFO::ICBUF: {
            send(2);
            break;
        }
    }
}

void BusMatrix::send(size_t slave_i) {
    if (!rd_i) {
        data_bo.write(slaves_data_bi[slave_i].get().read());
    }
    slaves_rd_o[slave_i].get().write(rd_i.read());
    slaves_wr_o[slave_i].get().write(wr_i.read());
}
