# CMakeLists.txt for app/src/main/cpp
cmake_minimum_required(VERSION 3.10.2)
project("flapmap_native")

# Path to headers we copied (adjust if you placed headers elsewhere)
# This assumes you copied OpenCV headers into app/src/main/cpp/include
set(OPENCV_INCLUDE_DIR ${CMAKE_SOURCE_DIR}/include)

# Add native library
add_library(native-lib SHARED native-lib.cpp)

# Find log library
find_library(log-lib log)

# Import OpenCV 3 prebuilt .so (use libopencv_java3.so from jniLibs/<ABI>/)
add_library(opencv_java3 SHARED IMPORTED)
set_target_properties(opencv_java3 PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libopencv_java3.so
)

# Include directories (if you copied headers)
target_include_directories(native-lib PRIVATE ${OPENCV_INCLUDE_DIR})

# Link libraries: log + OpenCV
target_link_libraries(native-lib PRIVATE ${log-lib} opencv_java3)
