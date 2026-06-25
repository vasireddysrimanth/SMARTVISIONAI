package com.devsrimanth.visionAi.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devsrimanth.visionAi.components.CameraPreview
import com.devsrimanth.visionAi.detections.DetectionMode
import com.devsrimanth.visionAi.fps.FpsCounter
import com.devsrimanth.visionAi.helpers.ObjectDetectorHelper
import com.devsrimanth.visionAi.helpers.ObjectOverlay
import com.devsrimanth.visionAi.helpers.PoseDetectorHelper
import com.devsrimanth.visionAi.helpers.PoseOverlay
import com.devsrimanth.visionAi.utils.UserPreferences
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.detector.Detection

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    when {
        cameraPermissionState.status.isGranted -> CameraContent()
        cameraPermissionState.status.shouldShowRationale -> {
            PermissionDeniedUI {
                cameraPermissionState.launchPermissionRequest()
            }
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Requesting camera permission...")
            }
        }
    }
}

@Composable
private fun CameraContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /** Persists confidence threshold across app restarts */
    val userPreferences = remember { UserPreferences(context) }

    var currentMode by remember { mutableStateOf(DetectionMode.POSE) }

    val fpsCounter = remember { FpsCounter() }
    var fpsText by remember { mutableStateOf("FPS: --") }

    /** Load saved threshold from DataStore, default 0.35 until loaded */
    val savedThreshold by userPreferences.confidenceThreshold.collectAsState(initial = 0.35f)
    var sliderValue by remember { mutableFloatStateOf(0.35f) }
    var showSlider by remember { mutableStateOf(false) }

    /** Sync slider with DataStore value on first load */
    LaunchedEffect(savedThreshold) {
        sliderValue = savedThreshold
    }

    var objectResults by remember { mutableStateOf<List<Detection>>(emptyList()) }
    var imageWidth by remember { mutableIntStateOf(1) }
    var imageHeight by remember { mutableIntStateOf(1) }

    val objectDetectorHelper = remember {
        ObjectDetectorHelper(
            context = context,
            onResult = { results, width, height ->
                objectResults = results
                imageWidth = width
                imageHeight = height
            },
            onError = { error -> Log.e("CameraScreen", error) }
        )
    }

    var poseResult by remember { mutableStateOf<PoseLandmarkerResult?>(null) }

    val poseDetectorHelper = remember {
        PoseDetectorHelper(
            context = context,
            onResult = { result, _, _ ->
                poseResult = result
            },
            onError = { error -> Log.e("CameraScreen", "Pose: $error") }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            objectDetectorHelper.clear()
            poseDetectorHelper.clear()
            fpsCounter.reset()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            currentMode = currentMode,
            objectDetectorHelper = objectDetectorHelper,
            poseDetectorHelper = poseDetectorHelper,
            onFrameProcessed = {
                fpsText = fpsCounter.calculate()
            }
        )

        if (currentMode == DetectionMode.OBJECT && objectResults.isNotEmpty()) {
            ObjectOverlay(
                results = objectResults,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                modifier = Modifier.fillMaxSize()
            )
        }

        val pose = poseResult
        if (currentMode == DetectionMode.POSE &&
            pose != null &&
            pose.landmarks().isNotEmpty()
        ) {
            PoseOverlay(
                result = pose,
                modifier = Modifier.fillMaxSize()
            )
        }

        /** Mode toggle — top center */
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetectionMode.entries.forEach { mode ->
                Button(
                    onClick = {
                        currentMode = mode
                        showSlider = false
                        fpsCounter.reset()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentMode == mode)
                            Color.White else Color.White.copy(alpha = 0.3f),
                        contentColor = if (currentMode == mode)
                            Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (mode == DetectionMode.OBJECT) "Object" else "Pose",
                        fontSize = 13.sp
                    )
                }
            }
        }

        /**
         * Confidence slider panel — only in OBJECT mode.
         * Pose mode doesn't support threshold slider.
         */
        if (showSlider && currentMode == DetectionMode.OBJECT) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
                    .padding(horizontal = 24.dp)
                    .background(
                        Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Min Confidence: ${"%.0f".format(sliderValue * 100)}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    /** Save to DataStore + recreate detector only when drag ends */
                    onValueChangeFinished = {
                        objectDetectorHelper.updateThreshold(sliderValue)
                        scope.launch {
                            userPreferences.saveConfidenceThreshold(sliderValue)
                        }
                    },
                    valueRange = 0.1f..0.9f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Green,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("10%", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text("90%", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }
        }

        /** Bottom row — mode label + slider toggle button */
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mode: ${currentMode.name}",
                color = Color.White,
                fontSize = 14.sp
            )

            Text(
                text = fpsText,
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            /** Slider toggle — only visible in OBJECT mode */
            if (currentMode == DetectionMode.OBJECT) {
                Button(
                    onClick = { showSlider = !showSlider },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showSlider)
                            Color.Green else Color.White.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚙️ ${"%.0f".format(sliderValue * 100)}%",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionDeniedUI(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Camera permission is required.")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Grant Permission") }
    }
}