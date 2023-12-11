//package com.dicoding.capstone_ch2ps254
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.ContentValues.TAG
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.SurfaceTexture
//import android.hardware.camera2.CameraCaptureSession
//import android.hardware.camera2.CameraDevice
//import android.hardware.camera2.CameraManager
//import android.os.Build.MODEL
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.Handler
//import android.os.HandlerThread
//import android.util.Log
//import android.view.Surface
//import android.view.TextureView
//import android.widget.ImageView
//import androidx.core.app.ActivityCompat
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.support.image.ImageProcessor
//import org.tensorflow.lite.support.image.TensorImage
//import org.tensorflow.lite.support.image.ops.ResizeOp
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import java.nio.charset.CodingErrorAction.REPLACE
//
//
//@Suppress("DEPRECATION")
//class CameraActivity : AppCompatActivity() {
//
//    val paint = Paint()
//    lateinit var imageProcessor: ImageProcessor
//    //    lateinit var model: null //(REPLACE THE MODEL)
//    lateinit var bitmap: Bitmap
//    lateinit var imageView: ImageView
//    lateinit var handler: Handler
//    lateinit var handlerThread: HandlerThread
//    lateinit var textureView: TextureView
//    lateinit var cameraManager: CameraManager
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camera)
//        Manifest.permission()
//
//        imageProcessor =
//            ImageProcessor.Builder().add(ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR)).build()
////        model = LiteModelMovenetSingleposeLightningTfliteFloat164.newInstance(this) (REPLACE THE MODEL)
//        imageView = findViewById(R.id.imageViewCamera)
//        textureView = findViewById(R.id.textureViewCamera)
//        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        handlerThread = HandlerThread("videoThread")
//        handlerThread.start()
//        handler = Handler(handlerThread.looper)
//
//        paint.setColor(Color.YELLOW)
//
//        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
//            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
//                open_camera()
//            }
//
//            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
//
//            }
//
//            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
//                return false
//            }
//
//            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
//                bitmap = textureView.bitmap!!
//                var tensorImage = TensorImage(DataType.UINT8)
//                tensorImage.load(bitmap)
//                tensorImage = imageProcessor.process(tensorImage)
//
//                val inputFeature =
//                    TensorBuffer.createFixedSize(intArrayOf(1, 192, 192, 3), DataType.UINT8)
//                inputFeature.loadBuffer(tensorImage.buffer)
//
//                val outputs = model.process(inputFeature)
//                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
//
//                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//                var canvas = Canvas(mutable)
//                var x = 0
//                var w = bitmap.width
//                var h = bitmap.height
//
//                Log.d(TAG, "output__" + outputFeature0.size.toString())
//                while (x <= 49) {
//                    if (outputFeature0.get(x + 2) > 0.45) {
//                        canvas.drawCircle(
//                            outputFeature0.get(x + 1) * w,
//                            outputFeature0.get(x) * h,
//                            10f,
//                            paint
//                        )
//                    }
//                    x += 3
//                }
//                imageView.setImageBitmap(mutable)
//            }
//        }
//    }
//
////    override fun onDestroy() {
////        super.onDestroy()
////        model.close()
////    }
//
//    fun open_camera() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        cameraManager.openCamera(
//            cameraManager.cameraIdList[0],
//            object : CameraDevice.StateCallback() {
//                override fun onOpened(p0: CameraDevice) {
//                    var captureRequest = p0.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//                    var surface = Surface(textureView.surfaceTexture)
//                    captureRequest.addTarget(surface)
//                    p0.createCaptureSession(
//                        listOf(surface),
//                        object : CameraCaptureSession.StateCallback() {
//                            override fun onConfigured(p0: CameraCaptureSession) {
//                                p0.setRepeatingRequest(captureRequest.build(), null, null)
//                            }
//
//                            override fun onConfigureFailed(session: CameraCaptureSession) {
//
//                            }
//                        }, handler
//                    )
//                }
//
//                override fun onDisconnected(camera: CameraDevice) {
//                    camera.close()
//                }
//
//                override fun onError(camera: CameraDevice, error: Int) {
//                    camera.close()
//                }
//            }, handler
//        )
//    }
//
//    fun get_permissions() {
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) get_permissions()
//    }
//}
