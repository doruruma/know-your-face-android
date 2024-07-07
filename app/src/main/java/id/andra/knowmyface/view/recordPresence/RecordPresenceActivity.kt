package id.andra.knowmyface.view.recordPresence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image.Plane
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import id.andra.knowmyface.R
import id.andra.knowmyface.databinding.ActivityRecordPresenceBinding
import id.andra.knowmyface.facerecognition.CameraConnectionFragment
import id.andra.knowmyface.facerecognition.FaceClassifier
import id.andra.knowmyface.facerecognition.FaceClassifier.Recognition
import id.andra.knowmyface.facerecognition.ImageUtils
import id.andra.knowmyface.facerecognition.TFLiteFaceRecognition
import id.andra.knowmyface.facerecognition.drawing.BorderedText
import id.andra.knowmyface.facerecognition.drawing.MultiBoxTracker
import id.andra.knowmyface.facerecognition.drawing.OverlayView
import id.andra.knowmyface.helper.BitmapHelper
import id.andra.knowmyface.helper.Constant
import id.andra.knowmyface.helper.Screen
import id.andra.knowmyface.helper.SharedPreferenceHelper
import id.andra.knowmyface.model.User
import id.andra.knowmyface.view.component.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RecordPresenceActivity : AppCompatActivity(), ImageReader.OnImageAvailableListener {

    private val viewModel: RecordPresenceViewModel by viewModels()
    private lateinit var binding: ActivityRecordPresenceBinding
    private var previewHeight: Int = 0
    private var previewWidth: Int = 0
    private var sensorOrientation: Int = 0
    private var borderedText: BorderedText? = null
    private var imageConverter: Runnable? = null
    private var postInferenceCallback: Runnable? = null
    private var rgbFrameBitmap: Bitmap? = null
    private var croppedBitmap: Bitmap? = null
    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null
    private var trackingOverlay: OverlayView? = null
    private lateinit var tracker: MultiBoxTracker
    private lateinit var faceClassifier: FaceClassifier
    private var rgbBytes: IntArray? = null
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var yRowStride = 0
    private var mappedRecognitions: ArrayList<Recognition>? = null
    private var detector: FaceDetector? = null
    private lateinit var user: User
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPresenceBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)
        tracker = MultiBoxTracker(this)
        val highAccuracyOpts =
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build()
        detector = FaceDetection.getClient(highAccuracyOpts)
        try {
            faceClassifier =
                TFLiteFaceRecognition.create(
                    assets,
                    "facenet.tflite",
                    Constant.TF_OD_API_INPUT_SIZE2,
                    false, applicationContext
                )
        } catch (e: IOException) {
            e.printStackTrace()
            val toast =
                Toast.makeText(
                    applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT
                )
            toast.show()
            finish()
        }
        user = SharedPreferenceHelper.getUser(this) ?: User()
        if (user.faceImage != null)
            getFaceImage(user.faceImage!!)
        if (intent.hasExtra("longitude"))
            viewModel.setLongitude(intent.getStringExtra("longitude").orEmpty())
        if (intent.hasExtra("latitude"))
            viewModel.setLatitude(intent.getStringExtra("latitude").orEmpty())
        if (intent.hasExtra("presenceState"))
            viewModel.setPresenceState(
                intent.getStringExtra("presenceState")?.toInt() ?: Constant.PRESENCE_UNDEFINED_STATE
            )
        initCameraPreview()
        observeState()
        setEventListeners()
    }

    override fun onImageAvailable(reader: ImageReader?) {
        if (previewWidth == 0 || previewHeight == 0)
            return
        if (rgbBytes == null)
            rgbBytes = IntArray(previewWidth * previewHeight)
        try {
            val image = reader!!.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter =
                Runnable {
                    ImageUtils.convertYUV420ToARGB8888(
                        yuvBytes[0],
                        yuvBytes[1],
                        yuvBytes[2],
                        previewWidth,
                        previewHeight,
                        yRowStride,
                        uvRowStride,
                        uvPixelStride,
                        rgbBytes
                    )
                }
            postInferenceCallback =
                Runnable {
                    image.close()
                    isProcessingFrame = false
                }
            performFaceDetection()
        } catch (e: Exception) {
            Log.d("tryError", e.message + "abc ")
            return
        }
    }

    private fun initCameraPreview() {
        val cameraManager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[CameraCharacteristics.LENS_FACING_BACK]
        val camera2Fragment =
            CameraConnectionFragment.newInstance(
                { size, rotation ->
                    previewHeight = size.height
                    previewWidth = size.width
                    val textSizePx =
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            Constant.TEXT_SIZE.toFloat(),
                            resources.displayMetrics
                        )
                    borderedText = BorderedText(textSizePx)
                    borderedText?.setTypeface(Typeface.MONOSPACE)
                    val cropSize: Int = Constant.CROP_SIZE
                    previewWidth = size.width
                    previewHeight = size.height
                    sensorOrientation = rotation - Screen.getScreenOrientation(this)
                    rgbFrameBitmap =
                        Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
                    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)
                    frameToCropTransform =
                        ImageUtils.getTransformationMatrix(
                            previewWidth,
                            previewHeight,
                            cropSize,
                            cropSize,
                            sensorOrientation,
                            false
                        )
                    cropToFrameTransform = Matrix()
                    frameToCropTransform?.invert(cropToFrameTransform)
                    trackingOverlay = findViewById<View>(R.id.tracking_overlay) as OverlayView
                    trackingOverlay?.addCallback { canvas ->
                        tracker.draw(canvas)
                    }
                    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation)
                },
                this,
                R.layout.fragment_camera,
                Size(640, 480)
            )
        camera2Fragment.setCamera(cameraId)
        fragmentManager.beginTransaction()
            .replace(
                R.id.container,
                camera2Fragment as android.app.Fragment
            ).commit()
    }

    private fun performFaceDetection() {
        imageConverter?.run()
        rgbFrameBitmap?.setPixels(rgbBytes!!, 0, previewWidth, 0, 0, previewWidth, previewHeight)
        val canvas = Canvas(croppedBitmap!!)
        canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)
        Handler().post {
            mappedRecognitions = ArrayList()
            val image = InputImage.fromBitmap(croppedBitmap!!, 0)
            detector
                ?.process(image)
                ?.addOnSuccessListener { faces ->
                    for (face in faces) {
                        performFaceRecognition(face)
                    }
                    tracker.trackResults(mappedRecognitions, 10)
                    trackingOverlay!!.postInvalidate()
                    postInferenceCallback!!.run()
                }
                ?.addOnFailureListener {
                    Log.d("FACE-ERROR", "${it.message}")
                }
        }
    }

    private fun performFaceRecognition(face: Face) {
        val bounds = face.boundingBox
        if (bounds.top < 0)
            bounds.top = 0
        if (bounds.left < 0)
            bounds.left = 0
        if (bounds.left + bounds.width() > croppedBitmap!!.width)
            bounds.right = croppedBitmap!!.width - 1
        if (bounds.top + bounds.height() > croppedBitmap!!.height)
            bounds.bottom = croppedBitmap!!.height - 1

        var crop = Bitmap.createBitmap(
            croppedBitmap!!,
            bounds.left,
            bounds.top,
            bounds.width(),
            bounds.height()
        )
        crop = Bitmap.createScaledBitmap(
            crop,
            Constant.TF_OD_API_INPUT_SIZE2,
            Constant.TF_OD_API_INPUT_SIZE2,
            false
        )

        val result = faceClassifier.recognizeImage(crop, false)
        var title: String? = "Tidak dikenali"
        var confidence = 0f
        if (result != null) {
            if (result.distance < 0.75f) {
                confidence = result.distance
                title = result.title
            }
        }
        val location = RectF(bounds)
        location.right = croppedBitmap!!.width - location.right
        location.left = croppedBitmap!!.width - location.left
        cropToFrameTransform!!.mapRect(location)
        val recognition =
            Recognition(face.trackingId.toString() + "", title, confidence, location)
        mappedRecognitions?.add(recognition)
        viewModel.setIsRecognized(title != "Tidak dikenali")
    }

    private fun detectFaceFromAPI(inputImage: Bitmap) {
        val image = InputImage.fromBitmap(inputImage, 0)
        detector
            ?.process(image)
            ?.addOnSuccessListener { faces ->
                for (face in faces) {
                    val bounds = face.boundingBox
                    recognizeFaceFromAPI(bounds, inputImage)
                }
            }
            ?.addOnFailureListener {
            }
    }

    private fun recognizeFaceFromAPI(bounds: Rect, inputImage: Bitmap) {
        if (bounds.top < 0) {
            bounds.top = 0
        }
        if (bounds.left < 0) {
            bounds.left = 0
        }
        if (bounds.right > inputImage.width) {
            bounds.right = inputImage.width - 1
        }
        if (bounds.bottom > inputImage.height) {
            bounds.bottom = inputImage.height - 1
        }
        var croppedFace = Bitmap.createBitmap(
            inputImage,
            bounds.left,
            bounds.top,
            bounds.width(),
            bounds.height()
        )
        croppedFace = Bitmap.createScaledBitmap(
            croppedFace,
            Constant.TF_OD_API_INPUT_SIZE2,
            Constant.TF_OD_API_INPUT_SIZE2,
            false
        )

        val result = faceClassifier.recognizeImage(croppedFace, true)
        if (result != null)
            faceClassifier.register(user.name, result)
    }

    private fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]!!]
        }
    }

    private fun getFaceImage(imgUrl: String) {
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapHelper.getBitmapFromURL("${Constant.APIENDPOINT}${imgUrl}")
            }
            if (bitmap != null)
                detectFaceFromAPI(bitmap)
        }
    }

    private fun toggleLoadingDialog(isShow: Boolean) {
        if (isShow) {
            loadingDialog = LoadingDialog()
            loadingDialog?.show(supportFragmentManager, "LOADING_DIALOG")
            return
        }
        loadingDialog?.dismiss()
    }

    private fun observeState() {
        viewModel.isLoading.observe(this) { value ->
            toggleLoadingDialog(value)
        }
        viewModel.presenceResponse.observe(this) { value ->
            if (value != null) {
                Toast.makeText(this, "Berhasil merekam absensi!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        viewModel.presenceError.observe(this) { value ->
            if (value != null && value.isNotEmpty()) {
                Toast.makeText(this, value, Toast.LENGTH_LONG).show()
                viewModel.setPresenceError("")
            }
        }
    }

    private fun setEventListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSubmit.setOnClickListener {
            if (!viewModel.isRecognized.value!!) {
                Toast.makeText(this, "Wajah tidak dikenali", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (rgbFrameBitmap == null) {
                Toast.makeText(this, "Sistem tidak valid", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.onSubmit(
                userId = "${user.id}",
                photo = BitmapHelper.bitmapToByteArray(rgbFrameBitmap!!).toList()
            )
        }
    }

}