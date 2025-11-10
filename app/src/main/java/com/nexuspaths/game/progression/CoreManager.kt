package com.nexuspaths.game.progression

import com.nexuspaths.game.models.NodeColor

/**
 * Manages all Nexus Cores and their progression
 */
class CoreManager {

    private val cores = NexusCore.createDefaultCores().toMutableList()

    /**
     * Get core by color
     */
    fun getCoreByColor(color: NodeColor): NexusCore? {
        return cores.find { it.color == color }
    }

    /**
     * Get all cores
     */
    fun getAllCores(): List<NexusCore> = cores

    /**
     * Get unlocked cores
     */
    fun getUnlockedCores(): List<NexusCore> = cores.filter { it.isUnlocked }

    /**
     * Add energy to a core
     */
    fun addEnergyToCore(color: NodeColor, amount: Int): Boolean {
        val core = getCoreByColor(color)
        return core?.addEnergy(amount) ?: false
    }

    /**
     * Unlock a core
     */
    fun unlockCore(color: NodeColor): Boolean {
        val core = getCoreByColor(color) ?: return false
        if (core.isUnlocked) return false

        core.isUnlocked = true
        return true
    }

    /**
     * Upgrade a core
     */
    fun upgradeCore(color: NodeColor): Boolean {
        val core = getCoreByColor(color) ?: return false
        return core.upgrade()
    }

    /**
     * Reset all cores (for new game)
     */
    fun resetEnergy() {
        cores.forEach { it.reset() }
    }

    /**
     * Check if any core is charged
     */
    fun hasChargedCore(): Boolean {
        return cores.any { it.isCharged }
    }

    /**
     * Get total cores unlocked
     */
    fun getUnlockedCount(): Int {
        return cores.count { it.isUnlocked }
    }
}
