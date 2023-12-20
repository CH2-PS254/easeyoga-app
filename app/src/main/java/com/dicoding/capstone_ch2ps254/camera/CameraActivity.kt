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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dicoding.capstone_ch2ps254.R
import com.dicoding.capstone_ch2ps254.ml.Posemodel
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt
import kotlin.math.max

@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {

    enum class Landmark {
        NOSE, LEFT_EYE, RIGHT_EYE, LEFT_EAR, RIGHT_EAR, LEFT_SHOULDER, RIGHT_SHOULDER,
        LEFT_ELBOW, RIGHT_ELBOW, LEFT_WRIST, RIGHT_WRIST, LEFT_HIP, RIGHT_HIP,
        LEFT_KNEE, RIGHT_KNEE, LEFT_ANKLE, RIGHT_ANKLE
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    lateinit var imageProcessor: ImageProcessor
    lateinit var model : AutoModel4
    lateinit var model1 : Posemodel
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
        model1 = Posemodel.newInstance(this)
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

        fun getCenterPoint(landmarks: FloatArray, leftBodypart: Landmark, rightBodypart: Landmark): FloatArray {
            val left = landmarks[leftBodypart.ordinal * 2]
            val right = landmarks[rightBodypart.ordinal * 2]
            val center = FloatArray(2)
            center[0] = left * 0.5f + right * 0.5f
            return center
        }

        fun getPoseSize(landmarks: FloatArray, torsoSizeMultiplier: Float = 2.5f): Float {
            val hipsCenter = getCenterPoint(landmarks, Landmark.LEFT_HIP, Landmark.RIGHT_HIP)
            val shouldersCenter = getCenterPoint(landmarks, Landmark.LEFT_SHOULDER, Landmark.RIGHT_SHOULDER)
            val torsoSize = sqrt(((shouldersCenter[0] - hipsCenter[0]) * (shouldersCenter[0] - hipsCenter[0]) +
                    (shouldersCenter[1] - hipsCenter[1]) * (shouldersCenter[1] - hipsCenter[1])))
            val poseCenterNew = getCenterPoint(landmarks, Landmark.LEFT_HIP, Landmark.RIGHT_HIP)
            val dX = landmarks[0] - poseCenterNew[0]
            val dY = landmarks[1] - poseCenterNew[1]
            var maxDist = sqrt(dX * dX + dY * dY)
            for (i in 2 until landmarks.size step 2) {
                val distance = sqrt((landmarks[i] - poseCenterNew[0]) * (landmarks[i] - poseCenterNew[0]) +
                        (landmarks[i + 1] - poseCenterNew[1]) * (landmarks[i + 1] - poseCenterNew[1]))
                if (distance > maxDist) {
                    maxDist = distance
                }
            }
            val poseSize = max(torsoSize * torsoSizeMultiplier, maxDist)
            return poseSize
        }

        fun normalizePoseLandmarks(landmarks: FloatArray): FloatArray {
            val poseCenter = getCenterPoint(landmarks, Landmark.LEFT_HIP, Landmark.RIGHT_HIP)
            val poseSize = getPoseSize(landmarks)
            for (i in landmarks.indices step 2) {
                landmarks[i] = (landmarks[i] - poseCenter[0]) / poseSize
                landmarks[i + 1] = (landmarks[i + 1] - poseCenter[1]) / poseSize
            }
            return landmarks
        }

        fun landmarksToEmbedding(landmarksAndScores: FloatArray): FloatArray {
            val landmarks = normalizePoseLandmarks(landmarksAndScores.copyOfRange(0, 34))
            return landmarks
        }

        fun preprocessData(newInputData: FloatArray): FloatArray {
            val processedNewInput = landmarksToEmbedding(newInputData)
            return processedNewInput
        }

        fun performInferenceOnNewData(newInputData: FloatArray): FloatArray {
            val interpreter = Interpreter(loadModelFileFromAssets("posemodel.tflite"))
            val inputShape = interpreter.getInputTensor(0).shape() // Get the input shape
            val inputBuffer = newInputData.copyOf(inputShape[1]) // Assuming input shape matches 1x51
            val outputBuffer = Array(1) { FloatArray(9) }
            interpreter.run(inputBuffer, outputBuffer)
            return outputBuffer[0]
        }


        fun preprocessAndInfer(newInputData: FloatArray): FloatArray {
            val processedInput = preprocessData(newInputData)
            val predictions = performInferenceOnNewData(processedInput)
            return predictions

        }

            val newInputData = (outputFeature0.floatArray)
            val predictions = preprocessAndInfer(newInputData)
            Timber.d(predictions.contentToString())


    }

    private fun loadModelFileFromAssets(modelFileName: String): ByteBuffer {
        val assetManager = assets
        val fileDescriptor = assetManager.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
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