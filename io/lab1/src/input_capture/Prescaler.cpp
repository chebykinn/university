#include "Prescaler.h"
#include "../InputCapture.h"

Prescaler::Prescaler(sc_module_name nm)
        :sc_module(nm),
         clk_i("clk_i"),
         icm_i("icm_i"),
         ins_i("ins_i"),
         ins_o("ins_o"),

         reg_counter(0)
{
    ins_o.initialize(0);

    SC_METHOD(on_clk);
    sensitive << clk_i.pos();
}

void Prescaler::on_clk() {
    auto temp_ins = ins_i.read();
    int temp_counter_threshold = get_counter_threshold();

    if (temp_counter_threshold == 0 || reg_counter >= temp_counter_threshold) {
        ins_o.write(0);
        reg_counter = 0;
    }
    if (temp_ins && reg_counter < temp_counter_threshold) {
        ins_o.write(1);
        reg_counter++;
    }
}

int Prescaler::get_counter_threshold() {
    switch (icm_i.read()) {
        case ICCONF::ICStoreAtPos:
        case ICCONF::ICStoreAtNeg:
        case ICCONF::ICStoreAtAny: {
            return 0x1;
        }
        case ICCONF::ICStoreAt4thPos:
        case ICCONF::ICStoreAt4thNeg: {
            return 0x4;
        }
        case ICCONF::ICStoreAt16thPos:
        case ICCONF::ICStoreAt16thNeg: {
            return 0x10;
        }
        default: {
            return 0;
        }
    }
}

