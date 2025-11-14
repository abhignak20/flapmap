package com.example.flapmap    // <-- make this match your actual package!

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // UI references
    private lateinit var btnPickImage: Button
    private lateinit var imageView: ImageView
    private lateinit var tvStatus: TextView
    private external fun processBitmapNative(bitmap: Bitmap)
    class MainActivity : AppCompatActivity() {
        companion object {
            init {
                System.loadLibrary("native-lib")
            }
        }
        // ...
    }


    // Load native libraries (make sure jniLibs contains libopencv_java4.so and native-lib.so)
    companion object {
        init {
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
    }

    // Declare native method
    private external fun processBitmapNative(bitmap: Bitmap)

    // ActivityResult API launcher for picking images
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            displayImageAndProcess(uri)
        } else {
            tvStatus.text = "Status: No image selected"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPickImage = findViewById(R.id.btnPickImage)
        imageView = findViewById(R.id.imageView)
        tvStatus = findViewById(R.id.tvStatus)

        btnPickImage.setOnClickListener {
            tvStatus.text = "Status: Picking image..."
            pickImageLauncher.launch(arrayOf("image/*"))
        }
    }

    // Decode Uri -> Bitmap, display, call native processing, update UI
    private fun displayImageAndProcess(uri: Uri) {
        try {
            // Decode bitmap (handle API differences)
            val decodedBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }

            // Ensure bitmap is mutable and in ARGB_8888 (required for AndroidBitmap locking in native)
            val argbBitmap = if (decodedBitmap.config == Bitmap.Config.ARGB_8888 && decodedBitmap.isMutable) {
                decodedBitmap
            } else {
                decodedBitmap.copy(Bitmap.Config.ARGB_8888, true)
            }

            // Show original (or immediately processed one if you want)
            imageView.setImageBitmap(argbBitmap)
            tvStatus.text = "Status: processing..."

            // Call native method (this modifies the bitmap in-place in our JNI example)
            processBitmapNative(argbBitmap)

            // Refresh the ImageView to show updated bitmap
            imageView.setImageBitmap(argbBitmap)
            imageView.invalidate()
            tvStatus.text = "Status: processed"

        } catch (e: IOException) {
            e.printStackTrace()
            tvStatus.text = "Status: Failed to load image"
        }
    }
}
