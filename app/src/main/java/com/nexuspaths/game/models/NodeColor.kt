package com.nexuspaths.game.models

import android.graphics.Color

/**
 * Represents the different energy types/colors in the game.
 * Each color corresponds to a Nexus Core type.
 */
enum class NodeColor(val colorValue: Int, val coreName: String) {
    RED(Color.rgb(255, 68, 68), "Warrior"),
    BLUE(Color.rgb(68, 68, 255), "Mage"),
    PURPLE(Color.rgb(170, 68, 255), "Rogue"),
    GREEN(Color.rgb(68, 255, 136), "Healer"),
    YELLOW(Color.rgb(255, 221, 68), "Artificer"),
    WILDCARD(Color.WHITE, "Universal");

    companion object {
        fun random(includeWildcard: Boolean = false): NodeColor {
            val colors = if (includeWildcard) values() else values().filter { it != WILDCARD }
            return colors.random()
        }

        fun getBasicColors() = listOf(RED, BLUE, PURPLE, GREEN, YELLOW)
    }

    fun getDarkerShade(): Int {
        val factor = 0.7f
        val r = (Color.red(colorValue) * factor).toInt()
        val g = (Color.green(colorValue) * factor).toInt()
        val b = (Color.blue(colorValue) * factor).toInt()
        return Color.rgb(r, g, b)
    }

    fun getLighterShade(): Int {
        val factor = 1.3f
        val r = (Color.red(colorValue) * factor).toInt().coerceAtMost(255)
        val g = (Color.green(colorValue) * factor).toInt().coerceAtMost(255)
        val b = (Color.blue(colorValue) * factor).toInt().coerceAtMost(255)
        return Color.rgb(r, g, b)
    }
}
