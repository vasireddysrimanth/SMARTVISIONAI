package com.devsrimanth.visionAi.helpers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import org.tensorflow.lite.task.vision.detector.Detection

/**
 * Draws bounding boxes + labels on top of camera preview.
 *
 * TFLite returns bounding box as RectF (left, top, right, bottom)
 * in pixel coordinates relative to the model input image size.
 * We scale these to actual canvas/screen size.
 */
@Composable
fun ObjectOverlay(
    results: List<Detection>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val textPaint = android.graphics.Paint().apply {
            color = Color.White.toArgb()
            textSize = 48f
            isFakeBoldText = true
            setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
        }

        for (detection in results) {
            val box = detection.boundingBox

            /**
             * TFLite model returns bounding box in pixel coords
             * relative to model input size (imageWidth x imageHeight).
             * Scale to canvas size for correct screen position.
             */
            val scaleX = canvasWidth / imageWidth.toFloat()
            val scaleY = canvasHeight / imageHeight.toFloat()

            val left   = box.left   * scaleX
            val top    = box.top    * scaleY
            val right  = box.right  * scaleX
            val bottom = box.bottom * scaleY

            /** Skip boxes that are clearly out of bounds */
            if (left >= canvasWidth || top >= canvasHeight ||
                right <= 0 || bottom <= 0) continue

            drawRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 6f)
            )

            val label = detection.categories.firstOrNull()
            if (label != null) {
                val text = "${label.label} ${"%.0f".format(label.score * 100)}%"
                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    left + 4f,
                    if (top > 60f) top - 12f else bottom + 48f,
                    textPaint
                )
            }
        }
    }
}