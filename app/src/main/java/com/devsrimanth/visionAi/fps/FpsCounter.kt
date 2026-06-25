
package com.devsrimanth.visionAi.fps

/**
 * Calculates FPS from frame timestamps.
 * Averages last 10 frames for smooth display.
 */
class FpsCounter {

    private val frameTimestamps = ArrayDeque<Long>(10)

    /**
     * Call this every frame.
     * Returns current FPS as a string.
     */
    fun calculate(): String {
        val now = System.currentTimeMillis()
        frameTimestamps.addLast(now)

        /** Keep only last 10 frame timestamps */
        while (frameTimestamps.size > 10) {
            frameTimestamps.removeFirst()
        }

        /** Need at least 2 frames to calculate FPS */
        if (frameTimestamps.size < 2) return "FPS: --"

        /** Average time between frames */
        val avgInterval = (frameTimestamps.last() - frameTimestamps.first())
            .toFloat() / (frameTimestamps.size - 1)

        val fps = if (avgInterval > 0) 1000f / avgInterval else 0f

        return "FPS: ${"%.1f".format(fps)}"
    }

    fun reset() = frameTimestamps.clear()
}