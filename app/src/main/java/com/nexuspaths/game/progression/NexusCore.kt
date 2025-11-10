package com.nexuspaths.game.progression

import com.nexuspaths.game.game.AbilityType
import com.nexuspaths.game.models.NodeColor

/**
 * Represents a Nexus Core - a character class that gains power from matching colors
 */
data class NexusCore(
    val color: NodeColor,
    val name: String,
    val abilityType: AbilityType,
    var level: Int = 1,
    var isUnlocked: Boolean = false,
    var currentEnergy: Int = 0,
    var maxEnergy: Int = 100
) {
    val isCharged: Boolean get() = currentEnergy >= maxEnergy

    /**
     * Add energy to the core
     */
    fun addEnergy(amount: Int): Boolean {
        if (!isUnlocked) return false

        currentEnergy = (currentEnergy + amount).coerceAtMost(maxEnergy)
        return isCharged
    }

    /**
     * Discharge the core (use ability)
     */
    fun discharge() {
        currentEnergy = 0
    }

    /**
     * Get energy percentage (0-1)
     */
    fun getEnergyPercentage(): Float {
        return currentEnergy.toFloat() / maxEnergy.toFloat()
    }

    /**
     * Upgrade the core
     */
    fun upgrade(): Boolean {
        if (level >= 3) return false
        level++
        maxEnergy = (maxEnergy * 1.5f).toInt()
        return true
    }

    /**
     * Get upgrade cost
     */
    fun getUpgradeCost(): Int {
        return when (level) {
            1 -> 500
            2 -> 1000
            else -> Int.MAX_VALUE
        }
    }

    /**
     * Reset energy (for new game)
     */
    fun reset() {
        currentEnergy = 0
    }

    companion object {
        /**
         * Create default cores
         */
        fun createDefaultCores(): List<NexusCore> {
            return listOf(
                NexusCore(
                    color = NodeColor.RED,
                    name = "Warrior Core",
                    abilityType = AbilityType.DESTROY_COLOR,
                    isUnlocked = true // Start with one unlocked
                ),
                NexusCore(
                    color = NodeColor.BLUE,
                    name = "Mage Core",
                    abilityType = AbilityType.SHUFFLE_BOARD,
                    isUnlocked = false
                ),
                NexusCore(
                    color = NodeColor.PURPLE,
                    name = "Rogue Core",
                    abilityType = AbilityType.CREATE_WILDCARDS,
                    isUnlocked = false
                ),
                NexusCore(
                    color = NodeColor.GREEN,
                    name = "Healer Core",
                    abilityType = AbilityType.TIME_EXTENSION,
                    isUnlocked = false
                ),
                NexusCore(
                    color = NodeColor.YELLOW,
                    name = "Artificer Core",
                    abilityType = AbilityType.SCORE_MULTIPLIER,
                    isUnlocked = false
                )
            )
        }

        /**
         * Get unlock cost for a core
         */
        fun getUnlockCost(coreIndex: Int): Int {
            return when (coreIndex) {
                0 -> 0 // First core is free
                1 -> 1000
                2 -> 2000
                3 -> 3000
                4 -> 5000
                else -> Int.MAX_VALUE
            }
        }
    }
}
