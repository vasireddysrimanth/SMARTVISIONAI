package com.devsrimanth.visionAi.helpers

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

@Composable
fun PoseOverlay(
    result: PoseLandmarkerResult?,
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean = true
) {

    if (result == null) return

    Canvas(modifier = modifier) {

        fun toOffset(x: Float, y: Float): Offset {

            val mappedX =
                if (isFrontCamera)
                    (1f - x) * size.width
                else
                    x * size.width

            return Offset(
                mappedX,
                y * size.height
            )
        }

        result.landmarks().forEach { landmarks ->

            // Draw skeleton
            POSE_CONNECTIONS.forEach { (startIdx, endIdx) ->

                if (startIdx >= landmarks.size ||
                    endIdx >= landmarks.size
                ) return@forEach

                val start = landmarks[startIdx]
                val end = landmarks[endIdx]

                drawLine(
                    color = Color(0xFF00E5FF),
                    start = toOffset(
                        start.x(),
                        start.y()
                    ),
                    end = toOffset(
                        end.x(),
                        end.y()
                    ),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }

            // Draw landmarks
            landmarks.forEachIndexed { index, landmark ->

                val center = toOffset(
                    landmark.x(),
                    landmark.y()
                )

                val color = when (index) {

                    // Face
                    in 0..10 ->
                        Color(0xFFFFEB3B)

                    // Left side
                    in listOf(
                        11, 13, 15,
                        17, 19, 21,
                        23, 25, 27,
                        29, 31
                    ) ->
                        Color(0xFF4CAF50)

                    // Right side
                    else ->
                        Color(0xFFFF5722)
                }

                // Outer border
                drawCircle(
                    color = Color.Black.copy(alpha = 0.4f),
                    radius = 10f,
                    center = center
                )

                // Inner dot
                drawCircle(
                    color = color,
                    radius = 7f,
                    center = center
                )

                // Optional ring
                drawCircle(
                    color = Color.White,
                    radius = 11f,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

/**
 * Clean Pose Skeleton
 */
private val POSE_CONNECTIONS = listOf(

    // Torso
    11 to 12,
    11 to 23,
    12 to 24,
    23 to 24,

    // Left arm
    11 to 13,
    13 to 15,

    // Right arm
    12 to 14,
    14 to 16,

    // Left leg
    23 to 25,
    25 to 27,

    // Right leg
    24 to 26,
    26 to 28,

    // Feet
    27 to 29,
    29 to 31,

    28 to 30,
    30 to 32
)