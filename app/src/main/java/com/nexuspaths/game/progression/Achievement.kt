package com.nexuspaths.game.progression

/**
 * Represents an achievement that can be unlocked
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val requirement: Int,
    var progress: Int = 0,
    var isUnlocked: Boolean = false,
    val rewardShards: Int = 100
) {
    val progressPercentage: Float
        get() = (progress.toFloat() / requirement.toFloat()).coerceIn(0f, 1f)

    fun updateProgress(amount: Int): Boolean {
        if (isUnlocked) return false

        progress += amount
        if (progress >= requirement) {
            isUnlocked = true
            return true
        }
        return false
    }

    companion object {
        fun createDefaultAchievements(): List<Achievement> {
            return listOf(
                Achievement(
                    id = "first_match",
                    title = "First Match",
                    description = "Complete your first match",
                    requirement = 1,
                    rewardShards = 50
                ),
                Achievement(
                    id = "10_games",
                    title = "Getting Started",
                    description = "Play 10 games",
                    requirement = 10,
                    rewardShards = 100
                ),
                Achievement(
                    id = "score_1000",
                    title = "Novice",
                    description = "Score 1000 points in a single game",
                    requirement = 1000,
                    rewardShards = 150
                ),
                Achievement(
                    id = "score_5000",
                    title = "Expert",
                    description = "Score 5000 points in a single game",
                    requirement = 5000,
                    rewardShards = 300
                ),
                Achievement(
                    id = "score_10000",
                    title = "Master",
                    description = "Score 10000 points in a single game",
                    requirement = 10000,
                    rewardShards = 500
                ),
                Achievement(
                    id = "combo_5x",
                    title = "Combo Master",
                    description = "Achieve a 5x combo multiplier",
                    requirement = 5,
                    rewardShards = 200
                ),
                Achievement(
                    id = "clear_100",
                    title = "Destroyer",
                    description = "Clear 100 nodes in total",
                    requirement = 100,
                    rewardShards = 100
                ),
                Achievement(
                    id = "clear_1000",
                    title = "Annihilator",
                    description = "Clear 1000 nodes in total",
                    requirement = 1000,
                    rewardShards = 300
                ),
                Achievement(
                    id = "unlock_all_cores",
                    title = "Core Collector",
                    description = "Unlock all Nexus Cores",
                    requirement = 5,
                    rewardShards = 500
                ),
                Achievement(
                    id = "max_upgrade",
                    title = "Fully Charged",
                    description = "Fully upgrade any core to level 3",
                    requirement = 1,
                    rewardShards = 400
                ),
                Achievement(
                    id = "star_match",
                    title = "Star Power",
                    description = "Create a star match pattern",
                    requirement = 1,
                    rewardShards = 150
                ),
                Achievement(
                    id = "line_match",
                    title = "Line Master",
                    description = "Create 10 line matches",
                    requirement = 10,
                    rewardShards = 200
                ),
                Achievement(
                    id = "daily_complete",
                    title = "Daily Dedication",
                    description = "Complete a daily challenge",
                    requirement = 1,
                    rewardShards = 150
                ),
                Achievement(
                    id = "play_hour",
                    title = "Dedicated Player",
                    description = "Play for a total of 1 hour",
                    requirement = 3600,
                    rewardShards = 300
                ),
                Achievement(
                    id = "shards_10000",
                    title = "Shard Hoarder",
                    description = "Collect 10,000 Nexus Shards",
                    requirement = 10000,
                    rewardShards = 1000
                )
            )
        }
    }
}
