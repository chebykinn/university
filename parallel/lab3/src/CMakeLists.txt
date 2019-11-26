cmake_minimum_required(VERSION 3.10.1)

project(lab3 LANGUAGES C VERSION 0.1.0)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)


find_package(OpenMP)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
add_executable(lab3 main.c)
target_compile_options(lab3 PRIVATE -O3 -Wall -Werror)
target_link_libraries(lab3 m)
if (OPENMP_FOUND)
    set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
    set (CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${OpenMP_EXE_LINKER_FLAGS}")
endif()
install(TARGETS lab3 RUNTIME DESTINATION "bin")

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
