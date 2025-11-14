#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>
#include <opencv2/opencv.hpp>

#define LOG_TAG "flapmap_native"
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

// JNI name must exactly match: Java_<package_with_underscores>_<ClassName>_<methodName>
JNIEXPORT void JNICALL
Java_com_example_flapmap_MainActivity_processBitmapNative(JNIEnv* env, jobject /* this */, jobject bitmap) {

    if (bitmap == nullptr) {
        ALOGE("bitmap is null");
        return;
    }

    AndroidBitmapInfo info;
    void* pixels = nullptr;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        ALOGE("AndroidBitmap_getInfo failed");
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 && info.format != ANDROID_BITMAP_FORMAT_RGB_565) {
        ALOGE("Unsupported bitmap format: %d", info.format);
        // We expect RGBA_8888 (ARGB_8888 in Java). Optionally convert on Java side.
        // Return silently to avoid crash.
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        ALOGE("AndroidBitmap_lockPixels failed");
        return;
    }

    // Create OpenCV Mat wrapping the bitmap pixels (no copy).
    // NOTE: RGBA_8888 on Android corresponds to CV_8UC4
    cv::Mat img(info.height, info.width, CV_8UC4, pixels);

    // Example processing: convert to grayscale and back to RGBA (visual effect)
    try {
        cv::Mat gray, out;
        cv::cvtColor(img, gray, cv::COLOR_RGBA2GRAY);
        cv::cvtColor(gray, out, cv::COLOR_GRAY2RGBA);
        out.copyTo(img); // copy processed data back to bitmap buffer
    } catch (const cv::Exception &e) {
        ALOGE("OpenCV exception: %s", e.what());
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

} // extern "C"
