package com.devsrimanth.visionAi.components

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.devsrimanth.visionAi.detections.DetectionMode
import com.devsrimanth.visionAi.helpers.ObjectDetectorHelper
import com.devsrimanth.visionAi.helpers.PoseDetectorHelper
import com.devsrimanth.visionAi.utils.imageProxyToBitmap
import java.util.concurrent.Executors

/**
 * Full screen CameraX preview.
 * Passes each frame to correct helper based on currentMode.
 * onFrameProcessed called every frame → drives FPS counter.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    currentMode: DetectionMode,
    objectDetectorHelper: ObjectDetectorHelper,
    poseDetectorHelper: PoseDetectorHelper,
    onFrameProcessed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val latestMode by rememberUpdatedState(currentMode)
    val latestOnFrameProcessed by rememberUpdatedState(onFrameProcessed)

    LaunchedEffect(Unit) {
        startCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            currentMode = { latestMode },
            objectDetectorHelper = objectDetectorHelper,
            poseDetectorHelper = poseDetectorHelper,
            onFrameProcessed = { latestOnFrameProcessed() }
        )
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    currentMode: () -> DetectionMode,
    objectDetectorHelper: ObjectDetectorHelper,
    poseDetectorHelper: PoseDetectorHelper,
    onFrameProcessed: () -> Unit           // ← new
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->

                    val bitmap = imageProxyToBitmap(imageProxy)
                    val rotation = imageProxy.imageInfo.rotationDegrees

                    when (currentMode()) {
                        DetectionMode.OBJECT -> {
                            objectDetectorHelper.detect(bitmap, rotation)
                        }
                        DetectionMode.POSE -> {
                            poseDetectorHelper.detect(bitmap, rotation)
                        }
                    }

                    /** Called every frame → FPS counter updates */
                    onFrameProcessed()

                    imageProxy.close()
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Camera bind failed", exc)
        }

    }, ContextCompat.getMainExecutor(context))
}