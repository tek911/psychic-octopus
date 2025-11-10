package com.nexuspaths.game.rendering

import android.graphics.*
import com.nexuspaths.game.models.NodeColor
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

/**
 * Factory for creating custom shader effects for different node types.
 * Each element (Warrior, Mage, Rogue, Healer, Artificer) has unique visual patterns.
 */
object ShaderFactory {

    /**
     * Creates an animated shader effect based on node color and time
     * @param color The node's element type
     * @param x Center X coordinate
     * @param y Center Y coordinate
     * @param size Node size
     * @param time Animation time in seconds
     * @return Shader with element-specific visual effects
     */
    fun createElementShader(
        color: NodeColor,
        x: Float,
        y: Float,
        size: Float,
        time: Float
    ): Shader {
        return when (color) {
            NodeColor.RED -> createWarriorShader(x, y, size, time)
            NodeColor.BLUE -> createMageShader(x, y, size, time)
            NodeColor.PURPLE -> createRogueShader(x, y, size, time)
            NodeColor.GREEN -> createHealerShader(x, y, size, time)
            NodeColor.YELLOW -> createArtificerShader(x, y, size, time)
            NodeColor.WILDCARD -> createWildcardShader(x, y, size, time)
        }
    }

    /**
     * RED WARRIOR - Fiery pulsing energy with volcanic core
     * Effect: Pulsating hot core with radiating heat waves
     */
    private fun createWarriorShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Animated pulse effect (0.8 to 1.2 scale)
        val pulse = 1f + sin(time * 3f) * 0.2f

        // Create intense gradient from bright orange core to deep red edges
        val centerColor = blendColors(
            Color.rgb(255, 160, 0),  // Bright orange
            Color.rgb(255, 68, 68),   // Base red
            sin(time * 2f) * 0.5f + 0.5f
        )

        return RadialGradient(
            x, y,
            size * pulse,
            intArrayOf(
                centerColor,                    // Animated hot core
                Color.rgb(255, 68, 68),        // Base red
                Color.rgb(180, 20, 20),        // Dark red
                Color.rgb(100, 10, 10)         // Nearly black edge
            ),
            floatArrayOf(0f, 0.4f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * BLUE MAGE - Mystical arcane energy with electric patterns
     * Effect: Swirling magical energy with shifting focus
     */
    private fun createMageShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Rotating center point for swirling effect
        val angle = time * 1.5f
        val offsetX = cos(angle) * size * 0.2f
        val offsetY = sin(angle) * size * 0.2f

        // Create mystical gradient with cyan highlights
        val highlightColor = blendColors(
            Color.rgb(150, 255, 255),  // Bright cyan
            Color.rgb(68, 68, 255),    // Base blue
            sin(time * 4f) * 0.5f + 0.5f
        )

        return RadialGradient(
            x + offsetX, y + offsetY,
            size,
            intArrayOf(
                highlightColor,              // Animated cyan core
                Color.rgb(100, 150, 255),   // Light blue
                Color.rgb(68, 68, 255),     // Base blue
                Color.rgb(30, 30, 150),     // Deep blue
                Color.rgb(10, 10, 80)       // Very dark blue
            ),
            floatArrayOf(0f, 0.3f, 0.6f, 0.85f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * PURPLE ROGUE - Shadowy mysterious energy with stealth effect
     * Effect: Shifting shadows with ethereal purple mist
     */
    private fun createRogueShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Asymmetric shadow effect
        val shadowX = sin(time * 2f) * size * 0.3f
        val shadowY = cos(time * 2f) * size * 0.3f

        // Dark mysterious gradient with occasional bright flashes
        val coreIntensity = if (sin(time * 5f) > 0.9f) 0.8f else 0.3f
        val coreColor = blendColors(
            Color.rgb(220, 180, 255),  // Bright purple (flash)
            Color.rgb(170, 68, 255),   // Base purple
            coreIntensity
        )

        return RadialGradient(
            x + shadowX, y + shadowY,
            size * 1.1f,
            intArrayOf(
                coreColor,                    // Animated core
                Color.rgb(140, 68, 200),     // Mid purple
                Color.rgb(100, 30, 150),     // Dark purple
                Color.rgb(60, 15, 90),       // Very dark purple
                Color.rgb(30, 5, 45)         // Nearly black
            ),
            floatArrayOf(0f, 0.35f, 0.6f, 0.85f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * GREEN HEALER - Life energy with gentle pulsing glow
     * Effect: Soothing organic pulse like a heartbeat
     */
    private fun createHealerShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Gentle heartbeat pulse (slower than warrior)
        val heartbeat = sin(time * 2f)
        val pulse = 1f + heartbeat * heartbeat * 0.15f // Square for gentle pulse

        // Warm, vibrant green with yellow-green highlights
        val glowIntensity = sin(time * 2f) * 0.5f + 0.5f
        val highlightColor = blendColors(
            Color.rgb(180, 255, 180),  // Pale green
            Color.rgb(68, 255, 136),   // Base green
            glowIntensity
        )

        return RadialGradient(
            x, y - size * 0.15f,  // Slight upward offset
            size * pulse,
            intArrayOf(
                highlightColor,              // Animated soft core
                Color.rgb(68, 255, 136),    // Base green
                Color.rgb(40, 200, 100),    // Mid green
                Color.rgb(20, 140, 70),     // Dark green
                Color.rgb(10, 80, 40)       // Very dark green
            ),
            floatArrayOf(0f, 0.4f, 0.65f, 0.85f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * YELLOW ARTIFICER - Mechanical/geometric energy with precise patterns
     * Effect: Crystalline structure with digital/tech feel
     */
    private fun createArtificerShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Sharp, precise transitions (less organic, more mechanical)
        val mechanicalPulse = (sin(time * 4f) * 0.5f + 0.5f) * 0.2f + 0.9f

        // Bright golden core with sharp contrasts
        val sparkleIntensity = if (cos(time * 8f) > 0.95f) 1f else 0.3f
        val coreColor = blendColors(
            Color.rgb(255, 255, 200),  // Bright white-gold
            Color.rgb(255, 221, 68),   // Base yellow
            sparkleIntensity
        )

        return RadialGradient(
            x, y,
            size * mechanicalPulse,
            intArrayOf(
                coreColor,                   // Sparkling core
                Color.rgb(255, 221, 68),    // Base yellow
                Color.rgb(220, 180, 30),    // Deep gold
                Color.rgb(180, 140, 20),    // Dark gold
                Color.rgb(100, 80, 10)      // Bronze edge
            ),
            floatArrayOf(0f, 0.35f, 0.6f, 0.82f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * WILDCARD - Rainbow shifting energy (universal connector)
     * Effect: Constantly shifting through spectrum colors
     */
    private fun createWildcardShader(x: Float, y: Float, size: Float, time: Float): Shader {
        // Create rainbow effect that cycles through hues
        val hue1 = (time * 60f) % 360f
        val hue2 = (time * 60f + 120f) % 360f
        val hue3 = (time * 60f + 240f) % 360f

        val color1 = Color.HSVToColor(floatArrayOf(hue1, 0.7f, 1f))
        val color2 = Color.HSVToColor(floatArrayOf(hue2, 0.8f, 1f))
        val color3 = Color.HSVToColor(floatArrayOf(hue3, 0.9f, 0.8f))

        // Pulsing size for extra dynamism
        val pulse = 1f + sin(time * 3f) * 0.15f

        return RadialGradient(
            x, y,
            size * pulse,
            intArrayOf(
                Color.WHITE,        // Bright white core
                color1,             // First rainbow color
                color2,             // Second rainbow color
                color3,             // Third rainbow color
                Color.DKGRAY       // Dark edge
            ),
            floatArrayOf(0f, 0.3f, 0.55f, 0.8f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * Creates a glow shader effect for selected/matched nodes
     */
    fun createGlowShader(
        color: NodeColor,
        x: Float,
        y: Float,
        size: Float,
        intensity: Float,
        time: Float
    ): Shader {
        val glowSize = size * (1f + intensity * 0.5f)

        // Element-specific glow colors
        val glowColor = when (color) {
            NodeColor.RED -> Color.rgb(255, 100, 50)
            NodeColor.BLUE -> Color.rgb(100, 200, 255)
            NodeColor.PURPLE -> Color.rgb(200, 100, 255)
            NodeColor.GREEN -> Color.rgb(100, 255, 150)
            NodeColor.YELLOW -> Color.rgb(255, 240, 100)
            NodeColor.WILDCARD -> {
                // Pulsing rainbow glow
                val hue = (time * 120f) % 360f
                Color.HSVToColor(floatArrayOf(hue, 0.6f, 1f))
            }
        }

        return RadialGradient(
            x, y,
            glowSize,
            intArrayOf(glowColor, Color.TRANSPARENT),
            floatArrayOf(0.2f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * Creates highlight shader for glossy effect
     */
    fun createHighlightShader(
        x: Float,
        y: Float,
        size: Float,
        alpha: Float
    ): Shader {
        return RadialGradient(
            x, y - size * 0.5f,
            size,
            intArrayOf(
                Color.WHITE,
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    /**
     * Blends two colors based on factor (0 = color1, 1 = color2)
     */
    private fun blendColors(color1: Int, color2: Int, factor: Float): Int {
        val clampedFactor = factor.coerceIn(0f, 1f)
        val invFactor = 1f - clampedFactor

        val r = (Color.red(color1) * invFactor + Color.red(color2) * clampedFactor).toInt()
        val g = (Color.green(color1) * invFactor + Color.green(color2) * clampedFactor).toInt()
        val b = (Color.blue(color1) * invFactor + Color.blue(color2) * clampedFactor).toInt()

        return Color.rgb(r, g, b)
    }
}
