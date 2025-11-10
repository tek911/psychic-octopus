package com.nexuspaths.game.game

import com.nexuspaths.game.models.HexCoord
import com.nexuspaths.game.models.Node
import com.nexuspaths.game.models.NodeColor
import kotlin.random.Random

/**
 * Manages the hexagonal grid of nodes.
 * Handles grid creation, node placement, and match detection.
 */
class HexGrid(val gridSize: Int = 7) {

    private val nodes = mutableMapOf<HexCoord, Node>()
    private val gridCoords: List<HexCoord>
    var hexSize: Float = 50f

    init {
        // Create a hexagonal grid
        gridCoords = HexCoord.createHexagonalGrid((gridSize - 1) / 2)
        initializeGrid()
    }

    /**
     * Initialize the grid with random nodes, ensuring no initial matches
     */
    private fun initializeGrid() {
        gridCoords.forEach { coord ->
            var color: NodeColor
            var attempts = 0
            do {
                color = NodeColor.random(false)
                attempts++
                // Prevent infinite loops
                if (attempts > 20) break
            } while (wouldCreateMatch(coord, color))

            val node = Node(coord, color)
            updateNodePosition(node)
            nodes[coord] = node
        }
    }

    /**
     * Check if placing a color at a coordinate would create a match
     */
    private fun wouldCreateMatch(coord: HexCoord, color: NodeColor): Boolean {
        val neighbors = coord.getNeighbors()
        val matchingNeighbors = neighbors.mapNotNull { nodes[it] }
            .filter { it.color == color }

        // Check if any two matching neighbors are adjacent to each other
        for (i in matchingNeighbors.indices) {
            for (j in i + 1 until matchingNeighbors.size) {
                if (matchingNeighbors[i].coord.isAdjacentTo(matchingNeighbors[j].coord)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Update node pixel position based on hex size and center offset
     */
    fun updateNodePosition(node: Node, centerX: Float = 0f, centerY: Float = 0f) {
        val (x, y) = node.coord.toPixel(hexSize)
        node.x = x + centerX
        node.y = y + centerY
    }

    /**
     * Update all node positions (called when screen size changes)
     */
    fun updateAllPositions(centerX: Float, centerY: Float) {
        nodes.values.forEach { updateNodePosition(it, centerX, centerY) }
    }

    /**
     * Get node at coordinate
     */
    fun getNode(coord: HexCoord): Node? = nodes[coord]

    /**
     * Get all nodes
     */
    fun getAllNodes(): List<Node> = nodes.values.toList()

    /**
     * Find node at pixel position
     */
    fun findNodeAtPosition(x: Float, y: Float): Node? {
        return nodes.values.minByOrNull { node ->
            val dx = node.x - x
            val dy = node.y - y
            dx * dx + dy * dy
        }?.takeIf { node ->
            val dx = node.x - x
            val dy = node.y - y
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
            distance <= hexSize
        }
    }

    /**
     * Find all matches starting from a given node using flood fill
     */
    fun findMatches(startNode: Node): Set<Node> {
        val visited = mutableSetOf<Node>()
        val toVisit = mutableListOf(startNode)
        val targetColor = startNode.color

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeAt(0)
            if (current in visited) continue

            visited.add(current)

            // Get matching neighbors
            val matchingNeighbors = current.coord.getNeighbors()
                .mapNotNull { nodes[it] }
                .filter { it.color == targetColor || it.color == NodeColor.WILDCARD }
                .filter { it !in visited }

            toVisit.addAll(matchingNeighbors)
        }

        // Only return if we have at least 3 matches
        return if (visited.size >= 3) visited else emptySet()
    }

    /**
     * Find all connected nodes of the same color
     */
    fun findConnectedGroup(node: Node): Set<Node> {
        val connected = mutableSetOf<Node>()
        val queue = mutableListOf(node)
        val targetColor = node.color

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current in connected) continue

            connected.add(current)

            val neighbors = current.coord.getNeighbors()
                .mapNotNull { nodes[it] }
                .filter {
                    (it.color == targetColor || it.color == NodeColor.WILDCARD) &&
                    it !in connected
                }

            queue.addAll(neighbors)
        }

        return connected
    }

    /**
     * Remove matched nodes and return them
     */
    fun removeNodes(nodesToRemove: Set<Node>): List<Node> {
        nodesToRemove.forEach { node ->
            nodes.remove(node.coord)
        }
        return nodesToRemove.toList()
    }

    /**
     * Fill empty spaces with new nodes
     */
    fun fillEmptySpaces(): List<Node> {
        val newNodes = mutableListOf<Node>()

        gridCoords.forEach { coord ->
            if (coord !in nodes) {
                val color = NodeColor.random(false)
                val node = Node(coord, color)
                updateNodePosition(node)
                node.alpha = 0 // Start invisible for fade-in animation
                nodes[coord] = node
                newNodes.add(node)
            }
        }

        return newNodes
    }

    /**
     * Shuffle the board (ability effect)
     */
    fun shuffle() {
        val colors = nodes.values.map { it.color }.shuffled()
        nodes.values.forEachIndexed { index, node ->
            node.color = colors[index]
            node.reset()
        }
    }

    /**
     * Clear all nodes of a specific color (ability effect)
     */
    fun clearColor(color: NodeColor): Set<Node> {
        return nodes.values.filter { it.color == color }.toSet()
    }

    /**
     * Convert random nodes to wildcards (ability effect)
     */
    fun createWildcards(count: Int): List<Node> {
        val eligibleNodes = nodes.values.filter { it.color != NodeColor.WILDCARD }
        return eligibleNodes.shuffled().take(count).onEach { it.color = NodeColor.WILDCARD }
    }

    /**
     * Check if any moves are possible
     */
    fun hasValidMoves(): Boolean {
        // Check each node to see if matching with a neighbor would create a match
        nodes.values.forEach { node ->
            val neighbors = node.coord.getNeighbors().mapNotNull { nodes[it] }
            neighbors.forEach { neighbor ->
                // Simulate swap
                val tempColor = node.color
                node.color = neighbor.color
                neighbor.color = tempColor

                val hasMatch = findMatches(node).size >= 3 || findMatches(neighbor).size >= 3

                // Swap back
                node.color = neighbor.color
                neighbor.color = tempColor

                if (hasMatch) return true
            }
        }
        return false
    }

    /**
     * Analyze the current board for special patterns
     */
    fun analyzePattern(matchedNodes: Set<Node>): MatchPattern {
        if (matchedNodes.size < 3) return MatchPattern.NONE

        val coords = matchedNodes.map { it.coord }

        // Check for star pattern (6 or more in circular arrangement)
        if (matchedNodes.size >= 6) {
            val center = coords.firstOrNull { coord ->
                coord.getNeighbors().count { it in coords } >= 5
            }
            if (center != null) return MatchPattern.STAR
        }

        // Check for line pattern (all in a row)
        if (matchedNodes.size >= 5) {
            val isLine = coords.all { it.q == coords.first().q } ||
                         coords.all { it.r == coords.first().r } ||
                         coords.all { it.s == coords.first().s }
            if (isLine) return MatchPattern.LINE
        }

        // Large cluster
        if (matchedNodes.size >= 7) return MatchPattern.CLUSTER

        // Regular match
        return MatchPattern.REGULAR
    }

    fun cleanup() {
        nodes.values.forEach { it.cleanup() }
    }
}

/**
 * Special pattern types that provide multipliers
 */
enum class MatchPattern(val multiplier: Float, val displayName: String) {
    NONE(1f, ""),
    REGULAR(1f, "Match!"),
    LINE(2f, "Line Match!"),
    CLUSTER(2.5f, "Cluster!"),
    STAR(3f, "Star Match!")
}
