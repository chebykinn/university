#include "EdgeDetector.h"
#include "ICCONF.h"

EdgeDetector::EdgeDetector(sc_module_name nm)
        :sc_module(nm),
         clk_i("clk_i"),
         icm_i("icm_i"),
         ins_i("ins_i"),
         ins_o("ins_o"),

         reg_prev_ins(0)
{
    ins_o.initialize(0);

    SC_METHOD(on_clk);
    sensitive << clk_i.pos();
}

void EdgeDetector::on_clk() {
    auto temp_ins = ins_i.read();
    auto temp_conf = get_mode();

    if ((reg_prev_ins != temp_ins) &&
            ((temp_conf == Any) || ((temp_conf == Pos) && temp_ins) || ((temp_conf == Neg) && !temp_ins))) {
        ins_o.write(1);
    } else {
        ins_o.write(0);
    }
    reg_prev_ins = temp_ins;
}

EdgeDetector::ed_icm_mode EdgeDetector::get_mode() {
    switch (icm_i.read()) {
        case ICCONF::ICStoreAtAny: {
            return Any;
        }
        case ICCONF::ICStoreAtNeg:
        case ICCONF::ICStoreAt4thNeg:
        case ICCONF::ICStoreAt16thNeg: {
            return Neg;
        }
        default: {
            return Pos;
        }
    }
}

