package com.nexuspaths.game.models

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Represents hexagonal coordinates using axial coordinate system.
 * This system uses q (column) and r (row) coordinates.
 */
data class HexCoord(val q: Int, val r: Int) {

    // Third coordinate for cubic coordinate system (useful for some calculations)
    val s: Int get() = -q - r

    /**
     * Returns all six neighbors of this hex coordinate
     */
    fun getNeighbors(): List<HexCoord> {
        return listOf(
            HexCoord(q + 1, r),      // Right
            HexCoord(q - 1, r),      // Left
            HexCoord(q, r + 1),      // Bottom-right
            HexCoord(q, r - 1),      // Top-left
            HexCoord(q + 1, r - 1),  // Top-right
            HexCoord(q - 1, r + 1)   // Bottom-left
        )
    }

    /**
     * Calculate distance to another hex coordinate
     */
    fun distanceTo(other: HexCoord): Int {
        return (abs(q - other.q) + abs(r - other.r) + abs(s - other.s)) / 2
    }

    /**
     * Check if this coordinate is adjacent to another
     */
    fun isAdjacentTo(other: HexCoord): Boolean {
        return distanceTo(other) == 1
    }

    /**
     * Get all hexes within a certain distance
     */
    fun getHexesInRange(range: Int): List<HexCoord> {
        val results = mutableListOf<HexCoord>()
        for (dq in -range..range) {
            for (dr in maxOf(-range, -dq - range)..minOf(range, -dq + range)) {
                results.add(HexCoord(q + dq, r + dr))
            }
        }
        return results
    }

    /**
     * Convert hex coordinates to pixel coordinates
     * size: the radius of the hexagon
     */
    fun toPixel(size: Float): Pair<Float, Float> {
        val x = size * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r).toFloat()
        val y = size * (3.0 / 2.0 * r).toFloat()
        return Pair(x, y)
    }

    companion object {
        /**
         * Create a hexagonal grid of given radius
         */
        fun createHexagonalGrid(radius: Int): List<HexCoord> {
            val coords = mutableListOf<HexCoord>()
            for (q in -radius..radius) {
                val r1 = maxOf(-radius, -q - radius)
                val r2 = minOf(radius, -q + radius)
                for (r in r1..r2) {
                    coords.add(HexCoord(q, r))
                }
            }
            return coords
        }

        /**
         * Create a rectangular hex grid
         */
        fun createRectangularGrid(width: Int, height: Int): List<HexCoord> {
            val coords = mutableListOf<HexCoord>()
            for (r in 0 until height) {
                val offset = r / 2
                for (q in -offset until width - offset) {
                    coords.add(HexCoord(q, r))
                }
            }
            return coords
        }
    }
}
