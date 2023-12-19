package com.dicoding.capstone_ch2ps254.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.dicoding.capstone_ch2ps254.ml.AutoModel4
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import android.graphics.*
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dicoding.capstone_ch2ps254.R
import timber.log.Timber


@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var model : AutoModel4
    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView
    lateinit var handler: Handler
    lateinit var handlerThread: HandlerThread
    lateinit var textureView: TextureView
    lateinit var cameraManager: CameraManager

    companion object {
        fun begin(context: Context) {
            val intent = Intent(context, CameraActivity::class.java)
            context.startActivity(intent)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        Manifest.permission()

        imageProcessor = ImageProcessor.Builder().add(ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = AutoModel4.newInstance(this)
        imageView = findViewById(R.id.imageView)
        textureView = findViewById(R.id.textureView)
        Timber.d("TextureView Initialized. Width: ${textureView.width}, Height: ${textureView.height}")

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

//        paint.color = Color.YELLOW

        checkCameraPermission()

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                // Open camera when TextureView is available
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                // Handle size change if needed
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                // Process the updated frame and update UI
                processAndDisplayFrame()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, open the camera
            openCamera()
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun openCamera() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (textureView.isAvailable) {
                    cameraManager.openCamera(
                        cameraManager.cameraIdList[0],
                        ContextCompat.getMainExecutor(this),
                        object : CameraDevice.StateCallback() {
                            override fun onOpened(camera: CameraDevice) {
                                val captureRequest =
                                    camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                val surface = Surface(textureView.surfaceTexture)
                                captureRequest.addTarget(surface)
                                camera.createCaptureSession(
                                    listOf(surface),
                                    object : CameraCaptureSession.StateCallback() {
                                        override fun onConfigured(session: CameraCaptureSession) {
                                            session.setRepeatingRequest(
                                                captureRequest.build(),
                                                null, null
                                            )
                                        }

                                        override fun onConfigureFailed(session: CameraCaptureSession) {
                                            // Handle configuration failure
                                        }
                                    },
                                    handler
                                )
                            }

                            override fun onDisconnected(camera: CameraDevice) {
                                // Handle camera disconnection
                            }

                            override fun onError(camera: CameraDevice, error: Int) {
                                // Handle camera error
                            }
                        }
                    )
                } else {
                    Timber.e("TextureView is not available yet.")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error opening camera")
        }
    }

    private fun processAndDisplayFrame() {
        bitmap = textureView.bitmap!!
        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 192, 192, 3), DataType.UINT8)
        inputFeature0.loadBuffer(tensorImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        Timber.d(outputFeature0.floatArray.contentToString())

        var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        var canvas = Canvas(mutable)
        var h = bitmap.height.toFloat()
        var w = bitmap.width.toFloat()
        var x = 0

        Timber.d(outputFeature0.flatSize.toString())

        while (x <= 49) {
            if (outputFeature0.getFloatValue(x + 2) > 0.45) {
                val cx = outputFeature0.getFloatValue(x + 1) * w
                val cy = outputFeature0.getFloatValue(x) * h
                canvas.drawCircle(cx, cy, 10f, Paint().apply { color = Color.YELLOW })
            }
            x += 3
        }
        imageView.setImageBitmap(mutable)
        Timber.d("TextureView Updated. Bitmap size: ${bitmap.width} x ${bitmap.height}")
        Timber.d("Output Feature0 flatSize: ${outputFeature0.flatSize}")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Timber.e("Camera permission denied.")
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}