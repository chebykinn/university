#include "TimersControl.h"
#include "ICCONF.h"

TimersControl::TimersControl(sc_module_name nm)
        :sc_module(nm),
         timer1_i("timer1_i"),
         timer2_i("timer2_i"),
         ictmr_i("ictmr_i"),
         tval_o("tval_o")
{
    tval_o.initialize(0);

    SC_METHOD(on_change);
    sensitive << timer1_i.value_changed() << timer2_i.value_changed() << ictmr_i.value_changed();
}

void TimersControl::on_change() {
    switch (ictmr_i.read()) {
        case ICCONF::ICTimersSecond: {
            tval_o.write(timer2_i.read());
            break;
        }
        default: {
            tval_o.write(timer1_i.read());
            break;
        }
    }
}

