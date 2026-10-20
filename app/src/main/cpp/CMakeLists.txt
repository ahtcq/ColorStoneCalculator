cmake_minimum_required(VERSION 3.22.1)
project(colorstone C)

add_library(calculator SHARED calculator.c)

target_link_libraries(calculator m)