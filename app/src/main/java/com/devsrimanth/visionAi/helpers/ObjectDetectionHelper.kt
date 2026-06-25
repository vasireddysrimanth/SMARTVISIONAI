package com.devsrimanth.visionAi.helpers

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    private val context: Context,
    private val onResult: (List<Detection>, Int, Int) -> Unit,
    private val onError: (String) -> Unit,
    var scoreThreshold: Float = 0.35f
) {
    private var objectDetector: ObjectDetector? = null

    init {
        val assets = context.assets.list("") ?: emptyArray()
        if (MODEL_FILE !in assets) onError("$MODEL_FILE not found")
        setup()
    }

    private fun setup() {
        if (trySetup(useGpu = true)) return
        if (trySetup(useGpu = false)) return
        onError("ObjectDetector setup failed")
    }

    private fun trySetup(useGpu: Boolean): Boolean = try {
        val baseOptions = if (useGpu) {
            BaseOptions.builder().useGpu().build()
        } else {
            BaseOptions.builder().setNumThreads(4).build()
        }

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(5)
            .setScoreThreshold(scoreThreshold)
            .build()

        objectDetector = ObjectDetector.createFromFileAndOptions(context, MODEL_FILE, options)
        true
    } catch (_: Exception) {
        false
    }

    fun detect(bitmap: Bitmap, imageRotation: Int) {
        val detector = objectDetector ?: return
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val results = detector.detect(tensorImage)
        onResult(results ?: emptyList(), tensorImage.width, tensorImage.height)
    }

    fun updateThreshold(threshold: Float) {
        scoreThreshold = threshold
        objectDetector?.close()
        objectDetector = null
        setup()
    }

    fun clear() {
        objectDetector?.close()
        objectDetector = null
    }

    companion object {
        private const val MODEL_FILE = "model.tflite"
    }
}