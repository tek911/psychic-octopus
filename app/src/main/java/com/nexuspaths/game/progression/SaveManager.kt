package com.nexuspaths.game.progression

import android.content.Context
import android.content.SharedPreferences
import com.nexuspaths.game.models.NodeColor
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages game persistence using SharedPreferences
 */
class SaveManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Save player progression
     */
    fun saveProgression(
        nexusShards: Int,
        highScore: Int,
        totalGamesPlayed: Int,
        cores: List<NexusCore>,
        achievements: List<Achievement>
    ) {
        prefs.edit().apply {
            putInt(KEY_SHARDS, nexusShards)
            putInt(KEY_HIGH_SCORE, highScore)
            putInt(KEY_GAMES_PLAYED, totalGamesPlayed)
            putString(KEY_CORES, serializeCores(cores))
            putString(KEY_ACHIEVEMENTS, serializeAchievements(achievements))
            apply()
        }
    }

    /**
     * Load nexus shards
     */
    fun loadNexusShards(): Int {
        return prefs.getInt(KEY_SHARDS, 0)
    }

    /**
     * Save nexus shards
     */
    fun saveNexusShards(amount: Int) {
        prefs.edit().putInt(KEY_SHARDS, amount).apply()
    }

    /**
     * Add nexus shards
     */
    fun addNexusShards(amount: Int) {
        val current = loadNexusShards()
        saveNexusShards(current + amount)
    }

    /**
     * Load high score
     */
    fun loadHighScore(): Int {
        return prefs.getInt(KEY_HIGH_SCORE, 0)
    }

    /**
     * Save high score if it's higher than current
     */
    fun saveHighScore(score: Int): Boolean {
        val currentHigh = loadHighScore()
        if (score > currentHigh) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply()
            return true
        }
        return false
    }

    /**
     * Load total games played
     */
    fun loadGamesPlayed(): Int {
        return prefs.getInt(KEY_GAMES_PLAYED, 0)
    }

    /**
     * Increment games played
     */
    fun incrementGamesPlayed() {
        val current = loadGamesPlayed()
        prefs.edit().putInt(KEY_GAMES_PLAYED, current + 1).apply()
    }

    /**
     * Load cores
     */
    fun loadCores(): List<NexusCore> {
        val json = prefs.getString(KEY_CORES, null) ?: return NexusCore.createDefaultCores()
        return deserializeCores(json)
    }

    /**
     * Save cores
     */
    fun saveCores(cores: List<NexusCore>) {
        prefs.edit().putString(KEY_CORES, serializeCores(cores)).apply()
    }

    /**
     * Load achievements
     */
    fun loadAchievements(): List<Achievement> {
        val json = prefs.getString(KEY_ACHIEVEMENTS, null) ?: return Achievement.createDefaultAchievements()
        return deserializeAchievements(json)
    }

    /**
     * Save achievements
     */
    fun saveAchievements(achievements: List<Achievement>) {
        prefs.edit().putString(KEY_ACHIEVEMENTS, serializeAchievements(achievements)).apply()
    }

    /**
     * Load statistics
     */
    fun loadStatistics(): GameStatistics {
        return GameStatistics(
            totalMatches = prefs.getInt(KEY_TOTAL_MATCHES, 0),
            totalNodesCleared = prefs.getInt(KEY_TOTAL_NODES, 0),
            highestCombo = prefs.getFloat(KEY_HIGHEST_COMBO, 1f),
            totalPlayTime = prefs.getLong(KEY_PLAY_TIME, 0)
        )
    }

    /**
     * Save statistics
     */
    fun saveStatistics(stats: GameStatistics) {
        prefs.edit().apply {
            putInt(KEY_TOTAL_MATCHES, stats.totalMatches)
            putInt(KEY_TOTAL_NODES, stats.totalNodesCleared)
            putFloat(KEY_HIGHEST_COMBO, stats.highestCombo)
            putLong(KEY_PLAY_TIME, stats.totalPlayTime)
            apply()
        }
    }

    /**
     * Get last daily challenge date
     */
    fun getLastDailyChallengeDate(): Long {
        return prefs.getLong(KEY_LAST_DAILY, 0)
    }

    /**
     * Set last daily challenge date
     */
    fun setLastDailyChallengeDate(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_DAILY, timestamp).apply()
    }

    /**
     * Clear all data (for testing)
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun serializeCores(cores: List<NexusCore>): String {
        val jsonArray = JSONArray()
        cores.forEach { core ->
            jsonArray.put(JSONObject().apply {
                put("color", core.color.name)
                put("level", core.level)
                put("unlocked", core.isUnlocked)
            })
        }
        return jsonArray.toString()
    }

    private fun deserializeCores(json: String): List<NexusCore> {
        val defaultCores = NexusCore.createDefaultCores().toMutableList()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val colorName = obj.getString("color")
                val level = obj.getInt("level")
                val unlocked = obj.getBoolean("unlocked")

                val core = defaultCores.find { it.color.name == colorName }
                core?.apply {
                    this.level = level
                    this.isUnlocked = unlocked
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultCores
    }

    private fun serializeAchievements(achievements: List<Achievement>): String {
        val jsonArray = JSONArray()
        achievements.forEach { achievement ->
            jsonArray.put(JSONObject().apply {
                put("id", achievement.id)
                put("unlocked", achievement.isUnlocked)
                put("progress", achievement.progress)
            })
        }
        return jsonArray.toString()
    }

    private fun deserializeAchievements(json: String): List<Achievement> {
        val defaultAchievements = Achievement.createDefaultAchievements().toMutableList()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val unlocked = obj.getBoolean("unlocked")
                val progress = obj.getInt("progress")

                val achievement = defaultAchievements.find { it.id == id }
                achievement?.apply {
                    this.isUnlocked = unlocked
                    this.progress = progress
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultAchievements
    }

    companion object {
        private const val PREFS_NAME = "nexus_paths_prefs"
        private const val KEY_SHARDS = "nexus_shards"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_GAMES_PLAYED = "games_played"
        private const val KEY_CORES = "cores"
        private const val KEY_ACHIEVEMENTS = "achievements"
        private const val KEY_TOTAL_MATCHES = "total_matches"
        private const val KEY_TOTAL_NODES = "total_nodes"
        private const val KEY_HIGHEST_COMBO = "highest_combo"
        private const val KEY_PLAY_TIME = "play_time"
        private const val KEY_LAST_DAILY = "last_daily"
    }
}

/**
 * Game statistics data class
 */
data class GameStatistics(
    var totalMatches: Int = 0,
    var totalNodesCleared: Int = 0,
    var highestCombo: Float = 1f,
    var totalPlayTime: Long = 0
)
