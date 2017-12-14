#pragma once

#include <systemc.h>

SC_MODULE(ICCONF) {
    enum icm_conf {
        ICOff               = 0x0,
        ICStoreAtAny        = 0x1,
        ICStoreAtNeg        = 0x2,
        ICStoreAtPos        = 0x3,
        ICStoreAt4thPos     = 0x4,
        ICStoreAt16thPos    = 0x5,
        ICStoreAt4thNeg     = 0x6,
        ICStoreAt16thNeg    = 0x7
    };

    enum icbne_conf {
        FIFOEmpty       = 0x0,
        FIFONotEmpty    = 0x1
    };

    enum icov_conf {
        FIFONotFull = 0x0,
        FIFOFull    = 0x1
    };

    enum ictmr_conf {
        ICTimersOff     = 0x0,
        ICTimersFirst   = 0x1,
        ICTimersSecond  = 0x2,
        ICTimersBoth    = 0x3
    };

    sc_in<sc_uint<32>>  icconf_i;
    sc_out<sc_uint<32>> icconf_o;
    sc_in<bool>         icbne_i;
    sc_in<bool>         icov_i;
    sc_out<sc_uint<3>>  icm_o;
    sc_out<sc_uint<2>>  ictmr_o;

    SC_HAS_PROCESS(ICCONF);

    ICCONF(sc_module_name nm);
    ~ICCONF() = default;

private:
    void on_change();

    sc_uint<32> reg_icconf;

public:
    static sc_uint<32> build_conf(ICCONF::icm_conf icm, ICCONF::ictmr_conf);
    static bool get_icbne(sc_uint<32> icconf);
    static bool get_icov(sc_uint<32> icconf);
};
