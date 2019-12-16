cmake_minimum_required(VERSION 3.10)

project(lab LANGUAGES C VERSION 0.1.0)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_C_COMPILER clang)

add_executable(lab src/pa23.c src/ipc.c src/msg.c src/util.c src/bank_robbery.c)
target_compile_options(lab PRIVATE -Wall -Werror -std=c99 -pedantic)
target_include_directories(lab PRIVATE include)
target_link_libraries(lab PRIVATE ${CMAKE_SOURCE_DIR}/lib64/libruntime.so)
