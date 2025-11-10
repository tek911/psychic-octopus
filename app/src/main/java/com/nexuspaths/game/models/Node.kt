package com.nexuspaths.game.models

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * Represents a single energy node on the hexagonal grid.
 * Handles its own rendering and animation state.
 */
class Node(
    val coord: HexCoord,
    var color: NodeColor,
    var isSelected: Boolean = false,
    var isMatched: Boolean = false
) {
    var x: Float = 0f
    var y: Float = 0f
    var scale: Float = 1f
    var alpha: Int = 255
    var glowIntensity: Float = 0f
    var rotationAngle: Float = 0f

    private var scaleAnimator: ValueAnimator? = null
    private var glowAnimator: ValueAnimator? = null

    /**
     * Draw the hexagonal node with gradient and glow effects
     */
    fun draw(canvas: Canvas, size: Float, paint: Paint) {
        val actualSize = size * scale

        // Draw glow effect if node is selected or matched
        if (isSelected || glowIntensity > 0) {
            drawGlow(canvas, actualSize, paint)
        }

        // Draw hexagon with gradient
        drawHexagon(canvas, actualSize, paint)

        // Draw inner highlight
        drawHighlight(canvas, actualSize * 0.6f, paint)
    }

    private fun drawGlow(canvas: Canvas, size: Float, paint: Paint) {
        val glowSize = size * (1f + glowIntensity * 0.5f)
        val gradient = RadialGradient(
            x, y, glowSize,
            intArrayOf(
                color.colorValue,
                android.graphics.Color.TRANSPARENT
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient
        paint.alpha = (alpha * 0.3f).toInt()
        canvas.drawCircle(x, y, glowSize, paint)
        paint.shader = null
    }

    private fun drawHexagon(canvas: Canvas, size: Float, paint: Paint) {
        val path = createHexagonPath(size)

        // Create gradient for the hexagon
        val gradient = RadialGradient(
            x, y - size * 0.3f, size,
            intArrayOf(
                color.getLighterShade(),
                color.colorValue,
                color.getDarkerShade()
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient
        paint.alpha = alpha
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

        // Draw hexagon border
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = if (isSelected) android.graphics.Color.WHITE else color.getDarkerShade()
        paint.alpha = alpha
        canvas.drawPath(path, paint)
    }

    private fun drawHighlight(canvas: Canvas, size: Float, paint: Paint) {
        val gradient = RadialGradient(
            x, y - size * 0.5f, size,
            intArrayOf(
                android.graphics.Color.WHITE,
                android.graphics.Color.TRANSPARENT
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient
        paint.alpha = (alpha * 0.4f).toInt()
        canvas.drawCircle(x, y - size * 0.3f, size, paint)
        paint.shader = null
    }

    private fun createHexagonPath(size: Float): Path {
        val path = Path()
        for (i in 0..6) {
            val angle = (60 * i + rotationAngle) * PI / 180
            val px = x + size * cos(angle).toFloat()
            val py = y + size * sin(angle).toFloat()
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        return path
    }

    /**
     * Animate node selection
     */
    fun animateSelection() {
        scaleAnimator?.cancel()
        scaleAnimator = ValueAnimator.ofFloat(scale, 1.2f, 1.1f).apply {
            duration = 200
            addUpdateListener { scale = it.animatedValue as Float }
            start()
        }

        glowAnimator?.cancel()
        glowAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { glowIntensity = it.animatedValue as Float }
            start()
        }
    }

    /**
     * Animate node deselection
     */
    fun animateDeselection() {
        scaleAnimator?.cancel()
        scaleAnimator = ValueAnimator.ofFloat(scale, 1f).apply {
            duration = 200
            addUpdateListener { scale = it.animatedValue as Float }
            start()
        }

        glowAnimator?.cancel()
        glowIntensity = 0f
    }

    /**
     * Animate node match/destruction
     */
    fun animateMatch(onComplete: () -> Unit) {
        val scaleAnim = ValueAnimator.ofFloat(scale, 1.3f, 0f).apply {
            duration = 400
            addUpdateListener { scale = it.animatedValue as Float }
        }

        val alphaAnim = ValueAnimator.ofInt(255, 0).apply {
            duration = 400
            addUpdateListener {
                alpha = it.animatedValue as Int
                if (it.animatedFraction >= 1f) {
                    onComplete()
                }
            }
        }

        val rotationAnim = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 400
            addUpdateListener { rotationAngle = it.animatedValue as Float }
        }

        scaleAnim.start()
        alphaAnim.start()
        rotationAnim.start()
    }

    /**
     * Reset node to default state
     */
    fun reset() {
        isSelected = false
        isMatched = false
        scale = 1f
        alpha = 255
        glowIntensity = 0f
        rotationAngle = 0f
        scaleAnimator?.cancel()
        glowAnimator?.cancel()
    }

    fun cleanup() {
        scaleAnimator?.cancel()
        glowAnimator?.cancel()
    }
}
