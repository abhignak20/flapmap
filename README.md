# flapmap
Flapmap AI Assignment – Android + Web Application
Overview
This project implements an Android application with NDK + OpenCV and a Web dashboard built with TypeScript.
The Android app processes camera frames using native C++ (JNI), and the web app displays or uploads processed results.

 Features Implemented
 Android App (NDK + OpenCV)
 Camera preview & image capture
 JNI bridge between Kotlin ↔ C++
 OpenCV C++ processing (e.g., grayscale / Canny edge detection / thresholding)
 Converts Bitmap ↔ Mat using OpenCV utilities
 Displays processed output in real-time

Saves processed images to storage

(Optional) Uploads processed image to web server

🌐 Web Dashboard (TypeScript)

Built using React + TypeScript

UI to upload and display processed images

Simple viewer with details section

(Optional) API endpoint to receive images from Android

📸 Screenshots / GIF

(Add your image files inside a folder called assets/ or screenshots/)

Android App
Web Dashboard
Demo GIF
 Setup Instructions
 Android Setup (NDK + OpenCV)
1. Requirements
Android Studio
Android SDK
Android NDK (r21+ recommended)
CMake
OpenCV Android SDK
2. Add OpenCV to Project
Download OpenCV for Android
Unzip

Copy the folder OpenCV-android-sdk/sdk/native/ into your project under app/src/main/cpp/include/ OR set path in CMake

3. Add to CMakeLists.txt
cmake_minimum_required(VERSION 3.10.2)
project("flapmap_native")

set(OPENCV_INCLUDE_DIR ${CMAKE_SOURCE_DIR}/include)
add_library(native-lib SHARED native-lib.cpp)
target_include_directories(native-lib PRIVATE ${OPENCV_INCLUDE_DIR})
find_library(log-lib log)
target_link_libraries(native-lib ${log-lib})
4. Load native library in Kotlin
companion object {
    init { System.loadLibrary("native-lib") }
}
5. JNI function example
external fun processFrame(bitmap: Bitmap): Bitmap
6. Grant Permissions
Add to AndroidManifest.xml:
<uses-permission android:name="android.permission.CAMERA" />
 Architecture Explanation

This project uses JNI, NDK, OpenCV, and TypeScript. Here is the high-level flow:
Frame Flow (Android)
Camera captures frame
Kotlin converts frame → Bitmap
Bitmap is passed to native code via JNI
Native C++ converts Bitmap → cv::Mat
OpenCV processes the frame
cv::Mat → Bitmap
Result sent back to Kotlin
UI updates ImageView with processed output

JNI Architecture
Kotlin/Java ↔ JNI Bridge ↔ C++ OpenCV Code


Java/Kotlin layer: UI, camera, permissions, lifecycle

JNI layer: Converts data types and calls native functions

C++ layer: OpenCV operations on Mat
 Web TypeScript Flow
User uploads image (or receives one from Android)
React displays processed image
(Optional) Backend stores image or additional metadata

How to Run the Project
Android App
Open project in Android Studio
Connect phone (USB debugging ON)
Click Run
Camera starts, processed output appears
Tap “Capture” to save image
(Optional) Tap “Upload” to send to web dashboard

 Web Dashboard
cd web
npm install
npm start

 Project Structure
├── app/
│   ├── src/main/cpp/
│   │   ├── native-lib.cpp
│   │   └── CMakeLists.txt
│   ├── src/main/java/com/example/
│   ├── src/main/res/
│   └── AndroidManifest.xml
├── web/
│   ├── src/
│   └── package.json
├── screenshots/
└── README.md

🧪 Testing
Tested on Android 10/11 devices
Verified OpenCV pipeline (Mat processing)
Web dashboard tested in Chrome
Commit History Requirement
This project includes multiple commits showing the development process:

Example commit structure:
init: android studio project
feat: add OpenCV + NDK setup
feat: implement JNI bridge
feat: add Canny edge detection
feat: create TypeScript web dashboard
docs: add screenshots and README

Known Issues
Camera frame rotation may vary across devices
JNI Bitmap conversions may cause slight performance overhead
Real-time streaming not implemented (only static processing)

Future Improvements
Add GPU acceleration
Add additional filters
Add backend database for storing uploaded images
Add WebSocket live streaming
