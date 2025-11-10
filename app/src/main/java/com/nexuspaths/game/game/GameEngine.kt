package com.nexuspaths.game.game

import com.nexuspaths.game.models.Node
import com.nexuspaths.game.models.NodeColor
import com.nexuspaths.game.progression.CoreManager
import kotlin.math.roundToInt

/**
 * Core game engine managing game state, scoring, and game loop
 */
class GameEngine(
    private val hexGrid: HexGrid,
    private val coreManager: CoreManager
) {
    private val selectedNodes = mutableListOf<Node>()
    private var gameState = GameState.PLAYING
    private var currentScore = 0
    private var timeRemaining = 60
    private var comboMultiplier = 1f
    private var lastMatchTime = 0L
    private val comboTimeout = 2000L // 2 seconds to continue combo

    var onScoreChanged: ((Int) -> Unit)? = null
    var onTimeChanged: ((Int) -> Unit)? = null
    var onComboChanged: ((Float) -> Unit)? = null
    var onEnergyCollected: ((NodeColor, Int) -> Unit)? = null
    var onGameOver: ((Int, Map<NodeColor, Int>) -> Unit)? = null
    var onMatchFound: ((Set<Node>, MatchPattern) -> Unit)? = null

    private val energyCollected = mutableMapOf<NodeColor, Int>()

    init {
        NodeColor.getBasicColors().forEach { energyCollected[it] = 0 }
    }

    /**
     * Start a new game
     */
    fun startGame() {
        gameState = GameState.PLAYING
        currentScore = 0
        timeRemaining = 60
        comboMultiplier = 1f
        energyCollected.clear()
        NodeColor.getBasicColors().forEach { energyCollected[it] = 0 }
        selectedNodes.clear()

        onScoreChanged?.invoke(currentScore)
        onTimeChanged?.invoke(timeRemaining)
    }

    /**
     * Update game timer (called every second)
     */
    fun updateTimer() {
        if (gameState != GameState.PLAYING) return

        timeRemaining--
        onTimeChanged?.invoke(timeRemaining)

        // Check combo timeout
        if (System.currentTimeMillis() - lastMatchTime > comboTimeout) {
            resetCombo()
        }

        if (timeRemaining <= 0) {
            endGame()
        }
    }

    /**
     * Handle node selection
     */
    fun selectNode(node: Node): Boolean {
        if (gameState != GameState.PLAYING) return false

        // First selection
        if (selectedNodes.isEmpty()) {
            selectedNodes.add(node)
            node.isSelected = true
            node.animateSelection()
            return true
        }

        // Same node clicked again - deselect
        if (node in selectedNodes) {
            deselectAll()
            return true
        }

        val firstNode = selectedNodes.first()

        // Adjacent node selected - check for match
        if (node.coord.isAdjacentTo(firstNode.coord)) {
            selectedNodes.add(node)
            node.isSelected = true
            node.animateSelection()

            // Check if we can continue the chain
            val canContinue = checkAndExtendChain(node)
            if (!canContinue) {
                // End of chain, process match
                processSelection()
            }
            return true
        } else {
            // Non-adjacent node - start new selection
            deselectAll()
            selectedNodes.add(node)
            node.isSelected = true
            node.animateSelection()
            return true
        }
    }

    /**
     * Check if the chain can be extended from the current node
     */
    private fun checkAndExtendChain(node: Node): Boolean {
        val color = selectedNodes.first().color
        val neighbors = node.coord.getNeighbors()
            .mapNotNull { hexGrid.getNode(it) }
            .filter { it !in selectedNodes }
            .filter { it.color == color || it.color == NodeColor.WILDCARD }

        return neighbors.isNotEmpty()
    }

    /**
     * Process the current selection for matches
     */
    private fun processSelection() {
        if (selectedNodes.size < 3) {
            deselectAll()
            return
        }

        val firstNode = selectedNodes.first()
        val matchedNodes = hexGrid.findConnectedGroup(firstNode)

        if (matchedNodes.size >= 3) {
            executeMatch(matchedNodes)
        } else {
            deselectAll()
        }
    }

    /**
     * Execute a match - remove nodes, add score, collect energy
     */
    private fun executeMatch(matchedNodes: Set<Node>) {
        gameState = GameState.MATCHING

        // Determine pattern and multiplier
        val pattern = hexGrid.analyzePattern(matchedNodes)
        val patternMultiplier = pattern.multiplier

        // Update combo
        lastMatchTime = System.currentTimeMillis()
        comboMultiplier = (comboMultiplier + 0.5f).coerceAtMost(5f)
        onComboChanged?.invoke(comboMultiplier)

        // Calculate score
        val baseScore = matchedNodes.size * 10
        val finalScore = (baseScore * patternMultiplier * comboMultiplier).roundToInt()
        currentScore += finalScore
        onScoreChanged?.invoke(currentScore)

        // Collect energy
        val color = matchedNodes.first().color
        if (color != NodeColor.WILDCARD) {
            val energyAmount = matchedNodes.size
            energyCollected[color] = (energyCollected[color] ?: 0) + energyAmount
            onEnergyCollected?.invoke(color, energyAmount)
        }

        // Notify about match
        onMatchFound?.invoke(matchedNodes, pattern)

        // Mark nodes as matched
        matchedNodes.forEach { it.isMatched = true }

        deselectAll()
        gameState = GameState.PLAYING
    }

    /**
     * Deselect all nodes
     */
    private fun deselectAll() {
        selectedNodes.forEach {
            it.isSelected = false
            it.animateDeselection()
        }
        selectedNodes.clear()
    }

    /**
     * Reset combo multiplier
     */
    private fun resetCombo() {
        if (comboMultiplier > 1f) {
            comboMultiplier = 1f
            onComboChanged?.invoke(comboMultiplier)
        }
    }

    /**
     * Use a core ability
     */
    fun useCoreAbility(color: NodeColor): Boolean {
        if (gameState != GameState.PLAYING) return false

        val core = coreManager.getCoreByColor(color)
        if (core == null || !core.isCharged) return false

        when (core.abilityType) {
            AbilityType.SHUFFLE_BOARD -> {
                hexGrid.shuffle()
            }
            AbilityType.DESTROY_COLOR -> {
                val nodesToRemove = hexGrid.clearColor(color)
                if (nodesToRemove.isNotEmpty()) {
                    executeMatch(nodesToRemove)
                }
            }
            AbilityType.CREATE_WILDCARDS -> {
                val count = 3 + core.level
                hexGrid.createWildcards(count)
            }
            AbilityType.TIME_EXTENSION -> {
                timeRemaining += 10 + (core.level * 5)
                onTimeChanged?.invoke(timeRemaining)
            }
            AbilityType.SCORE_MULTIPLIER -> {
                comboMultiplier = (comboMultiplier * 2f).coerceAtMost(10f)
                onComboChanged?.invoke(comboMultiplier)
            }
        }

        core.discharge()
        return true
    }

    /**
     * End the game
     */
    private fun endGame() {
        gameState = GameState.GAME_OVER
        onGameOver?.invoke(currentScore, energyCollected)
    }

    /**
     * Get current game state
     */
    fun getState(): GameState = gameState

    /**
     * Get current score
     */
    fun getScore(): Int = currentScore

    /**
     * Get time remaining
     */
    fun getTimeRemaining(): Int = timeRemaining

    /**
     * Get combo multiplier
     */
    fun getComboMultiplier(): Float = comboMultiplier

    /**
     * Get energy collected
     */
    fun getEnergyCollected(): Map<NodeColor, Int> = energyCollected.toMap()

    /**
     * Check if game is over
     */
    fun isGameOver(): Boolean = gameState == GameState.GAME_OVER
}

/**
 * Game state enumeration
 */
enum class GameState {
    PLAYING,
    MATCHING,
    GAME_OVER,
    PAUSED
}

/**
 * Core ability types
 */
enum class AbilityType(val displayName: String, val description: String) {
    SHUFFLE_BOARD("Shuffle", "Shuffle all nodes on the board"),
    DESTROY_COLOR("Destroy", "Destroy all nodes of this color"),
    CREATE_WILDCARDS("Wildcards", "Create wildcard nodes"),
    TIME_EXTENSION("Time Boost", "Add extra time"),
    SCORE_MULTIPLIER("Multiplier", "Double your combo multiplier")
}
