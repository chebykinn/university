cmake_minimum_required(VERSION 3.10)

project(lab LANGUAGES C VERSION 0.1.0)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_C_COMPILER clang)

add_executable(lab src/main.c src/util.c src/ipc.c)
target_compile_options(lab PRIVATE -Wall -Werror -std=c99 -pedantic)
target_include_directories(lab PRIVATE include)
