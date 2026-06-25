package com.devsrimanth.visionAi.helpers

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseDetectorHelper(
    private val context: Context,
    private val onResult: (PoseLandmarkerResult, Int, Int) -> Unit,
    private val onError: (String) -> Unit
) {

    private var poseLandmarker: PoseLandmarker? = null

    init {
        setUpPoseLandmarker()
    }

    private fun setUpPoseLandmarker() {
        try {
            /** GPU: MediaPipe uses Delegate.GPU, not useGpu() */
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker.task")
                .setDelegate(com.google.mediapipe.tasks.core.Delegate.GPU) // ← fix
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumPoses(1)
                .setMinPoseDetectionConfidence(0.5f)
                .setMinPosePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setResultListener { result, inputImage ->
                    onResult(result, inputImage.width, inputImage.height)
                }
                .setErrorListener { error ->
                    onError(error.message ?: "Unknown error")
                }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)

        } catch (e: Exception) {
            /** GPU failed → retry with CPU */
            Log.w(TAG, "GPU failed, retrying CPU: ${e.message}")
        }
    }

    fun detect(bitmap: Bitmap, imageRotation: Int) {
        val landmarker = poseLandmarker ?: return
        val mpImage = BitmapImageBuilder(bitmap).build()
        val frameTime = SystemClock.uptimeMillis()

        try {
            landmarker.detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
            onError("Detection error: ${e.message}")
            Log.e(TAG, "detect()", e)
        }
    }

    fun clear() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    companion object {
        private const val TAG = "PoseDetectorHelper"
    }
}