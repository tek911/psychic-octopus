package com.nexuspaths.game.models

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Color
import com.nexuspaths.game.rendering.ShaderFactory
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
    var animationTime: Float = 0f  // Time for shader animations

    private var scaleAnimator: ValueAnimator? = null
    private var glowAnimator: ValueAnimator? = null

    /**
     * Draw the hexagonal node with custom shaders and element-specific patterns
     */
    fun draw(canvas: Canvas, size: Float, paint: Paint) {
        val actualSize = size * scale

        // Draw glow effect if node is selected or matched
        if (isSelected || glowIntensity > 0) {
            drawGlow(canvas, actualSize, paint)
        }

        // Draw hexagon with custom element shader
        drawHexagon(canvas, actualSize, paint)

        // Draw element-specific pattern overlay
        drawElementPattern(canvas, actualSize, paint)

        // Draw inner highlight
        drawHighlight(canvas, actualSize * 0.6f, paint)
    }

    private fun drawGlow(canvas: Canvas, size: Float, paint: Paint) {
        val glowShader = ShaderFactory.createGlowShader(
            color, x, y, size, glowIntensity, animationTime
        )

        paint.shader = glowShader
        paint.alpha = (alpha * 0.4f).toInt()
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, size * (1f + glowIntensity * 0.5f), paint)
        paint.shader = null
    }

    private fun drawHexagon(canvas: Canvas, size: Float, paint: Paint) {
        val path = createHexagonPath(size)

        // Use custom element shader
        val shader = ShaderFactory.createElementShader(color, x, y, size, animationTime)

        paint.shader = shader
        paint.alpha = alpha
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

        // Draw hexagon border with enhanced glow for selected nodes
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = if (isSelected) 4f else 3f
        paint.color = if (isSelected) Color.WHITE else color.getDarkerShade()
        paint.alpha = alpha

        // Add shadow/depth to border
        if (isSelected) {
            paint.setShadowLayer(8f, 0f, 0f, Color.WHITE)
        }

        canvas.drawPath(path, paint)
        paint.clearShadowLayer()
    }

    private fun drawHighlight(canvas: Canvas, size: Float, paint: Paint) {
        val highlightShader = ShaderFactory.createHighlightShader(
            x, y, size, alpha / 255f
        )

        paint.shader = highlightShader
        paint.alpha = (alpha * 0.5f).toInt()
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y - size * 0.3f, size, paint)
        paint.shader = null
    }

    /**
     * Draw element-specific pattern overlay for visual distinction
     */
    private fun drawElementPattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.alpha = (alpha * 0.6f).toInt()

        when (color) {
            NodeColor.RED -> drawWarriorPattern(canvas, size, paint)
            NodeColor.BLUE -> drawMagePattern(canvas, size, paint)
            NodeColor.PURPLE -> drawRoguePattern(canvas, size, paint)
            NodeColor.GREEN -> drawHealerPattern(canvas, size, paint)
            NodeColor.YELLOW -> drawArtificerPattern(canvas, size, paint)
            NodeColor.WILDCARD -> drawWildcardPattern(canvas, size, paint)
        }
    }

    /**
     * RED WARRIOR - Radiating energy lines (aggressive, dynamic)
     */
    private fun drawWarriorPattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.strokeWidth = 2f
        paint.color = Color.rgb(255, 150, 100)

        // Draw 6 energy lines radiating from center (rotates with animation)
        for (i in 0..5) {
            val angle = (60 * i + animationTime * 30f) * PI / 180
            val innerRadius = size * 0.3f
            val outerRadius = size * (0.7f + sin(animationTime * 3f + i) * 0.1f)

            val x1 = x + innerRadius * cos(angle).toFloat()
            val y1 = y + innerRadius * sin(angle).toFloat()
            val x2 = x + outerRadius * cos(angle).toFloat()
            val y2 = y + outerRadius * sin(angle).toFloat()

            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // Draw pulsing inner circle
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        val pulseRadius = size * (0.25f + sin(animationTime * 4f) * 0.05f)
        canvas.drawCircle(x, y, pulseRadius, paint)
    }

    /**
     * BLUE MAGE - Mystical arcane circles (magical, precise)
     */
    private fun drawMagePattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.strokeWidth = 1.5f
        paint.color = Color.rgb(150, 200, 255)

        // Draw rotating arcane circles
        for (i in 1..2) {
            val radius = size * (0.3f + i * 0.2f)
            val rotation = animationTime * (if (i % 2 == 0) 1f else -1f) * 20f

            // Draw segmented circle (arcane rune appearance)
            for (segment in 0..5) {
                val startAngle = segment * 60f + rotation
                val sweepAngle = 40f  // Leaves gaps for mystical effect

                val rect = android.graphics.RectF(
                    x - radius, y - radius,
                    x + radius, y + radius
                )
                canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
            }
        }

        // Draw small mystical dots at cardinal points
        paint.style = Paint.Style.FILL
        for (i in 0..5) {
            val angle = (i * 60f + animationTime * 30f) * PI / 180
            val dotRadius = size * 0.55f
            val dotX = x + dotRadius * cos(angle).toFloat()
            val dotY = y + dotRadius * sin(angle).toFloat()
            canvas.drawCircle(dotX, dotY, 3f, paint)
        }
    }

    /**
     * PURPLE ROGUE - Shadow streaks (stealthy, mysterious)
     */
    private fun drawRoguePattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.strokeWidth = 3f
        paint.color = Color.rgb(200, 120, 255)
        paint.alpha = (alpha * 0.4f).toInt()

        // Draw asymmetric shadow streaks
        for (i in 0..3) {
            val angle = (i * 90f + animationTime * 15f) * PI / 180
            val offset = sin(animationTime * 2f + i) * size * 0.2f

            val startRadius = size * 0.2f
            val endRadius = size * 0.6f

            val x1 = x + startRadius * cos(angle).toFloat() + offset
            val y1 = y + startRadius * sin(angle).toFloat()
            val x2 = x + endRadius * cos(angle).toFloat() + offset
            val y2 = y + endRadius * sin(angle).toFloat()

            // Gradient effect by drawing multiple lines with decreasing alpha
            paint.alpha = (alpha * 0.5f).toInt()
            canvas.drawLine(x1, y1, x2, y2, paint)
            paint.alpha = (alpha * 0.3f).toInt()
            canvas.drawLine(x1 + 2f, y1, x2 + 2f, y2, paint)
        }

        // Draw subtle corner accents
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.alpha = (alpha * 0.6f).toInt()
        for (i in 0..5 step 2) {
            val angle = (i * 60f) * PI / 180
            val radius = size * 0.65f
            val accentX = x + radius * cos(angle).toFloat()
            val accentY = y + radius * sin(angle).toFloat()
            canvas.drawCircle(accentX, accentY, 4f, paint)
        }
    }

    /**
     * GREEN HEALER - Organic life patterns (natural, flowing)
     */
    private fun drawHealerPattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.strokeWidth = 2f
        paint.color = Color.rgb(120, 255, 180)
        paint.style = Paint.Style.STROKE

        // Draw organic pulsing cross (like a medical symbol but natural)
        val pulse = sin(animationTime * 2f) * 0.1f + 1f
        val lineLength = size * 0.5f * pulse

        // Vertical life line
        canvas.drawLine(x, y - lineLength, x, y + lineLength, paint)
        // Horizontal life line
        canvas.drawLine(x - lineLength, y, x + lineLength, y, paint)

        // Draw small leaves/petals around the center
        paint.style = Paint.Style.FILL
        paint.alpha = (alpha * 0.5f).toInt()
        for (i in 0..3) {
            val angle = (i * 90f + 45f + animationTime * 20f) * PI / 180
            val leafDistance = size * 0.4f
            val leafX = x + leafDistance * cos(angle).toFloat()
            val leafY = y + leafDistance * sin(angle).toFloat()

            // Draw small leaf shape (circle approximation)
            val leafSize = 5f + sin(animationTime * 3f + i) * 2f
            canvas.drawCircle(leafX, leafY, leafSize, paint)
        }

        // Draw gentle pulsing aura circle
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.alpha = (alpha * (0.3f + sin(animationTime * 2f) * 0.2f)).toInt()
        canvas.drawCircle(x, y, size * 0.6f * pulse, paint)
    }

    /**
     * YELLOW ARTIFICER - Geometric/crystalline patterns (technological, precise)
     */
    private fun drawArtificerPattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.strokeWidth = 2f
        paint.color = Color.rgb(255, 240, 150)
        paint.style = Paint.Style.STROKE

        // Draw rotating geometric grid
        val rotation = animationTime * 40f

        // Inner triangle
        val innerPath = Path()
        for (i in 0..2) {
            val angle = (i * 120f + rotation) * PI / 180
            val radius = size * 0.3f
            val px = x + radius * cos(angle).toFloat()
            val py = y + radius * sin(angle).toFloat()
            if (i == 0) innerPath.moveTo(px, py) else innerPath.lineTo(px, py)
        }
        innerPath.close()
        canvas.drawPath(innerPath, paint)

        // Outer triangle (counter-rotating)
        val outerPath = Path()
        for (i in 0..2) {
            val angle = (i * 120f - rotation) * PI / 180
            val radius = size * 0.55f
            val px = x + radius * cos(angle).toFloat()
            val py = y + radius * sin(angle).toFloat()
            if (i == 0) outerPath.moveTo(px, py) else outerPath.lineTo(px, py)
        }
        outerPath.close()
        canvas.drawPath(outerPath, paint)

        // Draw connecting lines between triangles
        paint.alpha = (alpha * 0.4f).toInt()
        for (i in 0..2) {
            val angle = (i * 120f) * PI / 180
            val innerRadius = size * 0.3f
            val outerRadius = size * 0.55f
            val x1 = x + innerRadius * cos(angle).toFloat()
            val y1 = y + innerRadius * sin(angle).toFloat()
            val x2 = x + outerRadius * cos(angle).toFloat()
            val y2 = y + outerRadius * sin(angle).toFloat()
            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // Sparkle effect at corners
        if (sin(animationTime * 6f) > 0.9f) {
            paint.style = Paint.Style.FILL
            paint.alpha = (alpha * 0.8f).toInt()
            val sparkleAngle = ((animationTime * 6f).toInt() % 3) * 120f * PI / 180
            val sparkleRadius = size * 0.55f
            val sparkleX = x + sparkleRadius * cos(sparkleAngle).toFloat()
            val sparkleY = y + sparkleRadius * sin(sparkleAngle).toFloat()
            canvas.drawCircle(sparkleX, sparkleY, 4f, paint)
        }
    }

    /**
     * WILDCARD - Rainbow particles (universal, dynamic)
     */
    private fun drawWildcardPattern(canvas: Canvas, size: Float, paint: Paint) {
        paint.style = Paint.Style.FILL

        // Draw orbiting rainbow particles
        for (i in 0..5) {
            val hue = ((animationTime * 60f + i * 60f) % 360f)
            paint.color = Color.HSVToColor(floatArrayOf(hue, 0.8f, 1f))
            paint.alpha = (alpha * 0.7f).toInt()

            val orbitAngle = (animationTime * 60f + i * 60f) * PI / 180
            val orbitRadius = size * (0.45f + sin(animationTime * 3f + i) * 0.1f)

            val particleX = x + orbitRadius * cos(orbitAngle).toFloat()
            val particleY = y + orbitRadius * sin(orbitAngle).toFloat()

            canvas.drawCircle(particleX, particleY, 4f, paint)

            // Draw trailing glow
            paint.alpha = (alpha * 0.3f).toInt()
            canvas.drawCircle(particleX, particleY, 6f, paint)
        }

        // Draw center sparkle
        paint.color = Color.WHITE
        paint.alpha = (alpha * (0.6f + sin(animationTime * 5f) * 0.4f)).toInt()
        canvas.drawCircle(x, y, 5f, paint)

        // Draw rotating star shape in center
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.rgb(255, 255, 255)
        paint.alpha = (alpha * 0.5f).toInt()

        val starPath = Path()
        for (i in 0..7) {
            val angle = (i * 45f + animationTime * 90f) * PI / 180
            val radius = if (i % 2 == 0) size * 0.15f else size * 0.25f
            val px = x + radius * cos(angle).toFloat()
            val py = y + radius * sin(angle).toFloat()
            if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
        }
        starPath.close()
        canvas.drawPath(starPath, paint)
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
