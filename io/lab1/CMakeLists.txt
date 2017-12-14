cmake_minimum_required(VERSION 3.8)

project(lab1)

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -stdlib=libc++ ")

add_executable(lab1
        src/input_capture/ICCONF.cpp
        src/input_capture/Prescaler.cpp
        src/input_capture/EdgeDetector.cpp
        src/input_capture/TimersControl.cpp
        src/input_capture/FIFO.cpp
        src/InputCapture.cpp
        src/BusMatrix.cpp
        src/main.cpp
        src/mips32.cpp
        src/Timer.cpp
        src/Signal.cpp
        )

add_library(systemc SHARED IMPORTED)

set_target_properties(systemc PROPERTIES
        IMPORTED_LOCATION "/Users/dmaituganov/Documents/labs/systemc/systemc-2.3.1a/lib/libsystemc.dylib"
        INTERFACE_INCLUDE_DIRECTORIES "/Users/dmaituganov/Documents/labs/systemc/systemc-2.3.1a/include"
        )

set_target_properties(lab1 PROPERTIES
        ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
        )

target_link_libraries(lab1 m systemc)