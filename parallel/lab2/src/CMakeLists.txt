cmake_minimum_required(VERSION 3.10.1)

project(lab2 LANGUAGES C VERSION 0.1.0)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/framewave)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/framewave/lib)

add_executable(lab2-par main.c)
target_compile_options(lab2-par PRIVATE -m64 -g -Wall -Werror)
target_link_libraries(lab2-par m fwBase fwSignal)

install(TARGETS lab2-par RUNTIME DESTINATION "bin")

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
