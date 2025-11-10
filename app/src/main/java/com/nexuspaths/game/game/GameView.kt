package com.nexuspaths.game.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.nexuspaths.game.graphics.ParticleSystem
import com.nexuspaths.game.models.Node
import com.nexuspaths.game.models.NodeColor
import com.nexuspaths.game.progression.CoreManager
import kotlin.math.min

/**
 * Custom view that renders the game using Canvas
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val hexGrid = HexGrid(7)
    private val coreManager = CoreManager()
    private val gameEngine = GameEngine(hexGrid, coreManager)
    private val particleSystem = ParticleSystem()
    private val soundManager = SoundManager(context)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 40f
        color = Color.WHITE
    }

    private var centerX = 0f
    private var centerY = 0f
    private var lastFrameTime = System.currentTimeMillis()
    private var gameStartTime = 0L
    private var lastSecondUpdate = 0L

    var onGameOver: ((Int, Map<NodeColor, Int>) -> Unit)? = null

    init {
        setupGameEngine()
        startGame()
    }

    private fun setupGameEngine() {
        gameEngine.onScoreChanged = { invalidate() }
        gameEngine.onTimeChanged = { invalidate() }
        gameEngine.onComboChanged = { invalidate() }

        gameEngine.onEnergyCollected = { color, amount ->
            coreManager.addEnergyToCore(color, amount)
            invalidate()
        }

        gameEngine.onMatchFound = { nodes, pattern ->
            // Create particle effects
            nodes.forEach { node ->
                particleSystem.createExplosion(node.x, node.y, node.color.colorValue, 15)
            }

            // Play sound
            soundManager.playMatchSound(nodes.size, pattern)

            // Animate matched nodes
            nodes.forEach { node ->
                node.animateMatch {
                    hexGrid.removeNodes(setOf(node))
                    val newNodes = hexGrid.fillEmptySpaces()
                    newNodes.forEach { it.alpha = 0 }
                    invalidate()
                }
            }
        }

        gameEngine.onGameOver = { score, energy ->
            soundManager.playGameOverSound()
            onGameOver?.invoke(score, energy)
        }
    }

    fun startGame() {
        gameEngine.startGame()
        coreManager.resetEnergy()
        gameStartTime = System.currentTimeMillis()
        lastSecondUpdate = gameStartTime
        invalidate()
    }

    fun getGameEngine(): GameEngine = gameEngine
    fun getCoreManager(): CoreManager = coreManager
    fun getSoundManager(): SoundManager = soundManager

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calculate center position and hex size
        centerX = w / 2f
        centerY = h / 2f - 100f

        // Calculate optimal hex size based on screen size
        val gridRadius = 3.5f
        val availableWidth = w * 0.9f
        val availableHeight = (h - 300) * 0.9f

        hexGrid.hexSize = min(availableWidth, availableHeight) / (gridRadius * 4f)
        hexGrid.updateAllPositions(centerX, centerY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Update time
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastFrameTime) / 1000f
        lastFrameTime = currentTime

        // Update timer every second
        if (currentTime - lastSecondUpdate >= 1000) {
            gameEngine.updateTimer()
            lastSecondUpdate = currentTime
        }

        // Draw background
        drawBackground(canvas)

        // Draw UI
        drawHUD(canvas)

        // Draw cores
        drawCores(canvas)

        // Draw grid nodes
        hexGrid.getAllNodes().forEach { node ->
            // Fade in new nodes
            if (node.alpha < 255) {
                node.alpha = (node.alpha + deltaTime * 500).toInt().coerceAtMost(255)
            }
            node.draw(canvas, hexGrid.hexSize, paint)
        }

        // Draw particles
        particleSystem.update(deltaTime)
        particleSystem.draw(canvas, paint)

        // Continue animating
        if (!gameEngine.isGameOver() || particleSystem.getParticleCount() > 0) {
            invalidate()
        }
    }

    private fun drawBackground(canvas: Canvas) {
        // Gradient background
        canvas.drawColor(Color.rgb(15, 15, 30))

        // Draw animated grid pattern
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(30, 255, 255, 255)

        val time = (System.currentTimeMillis() / 50f) % 360f
        for (i in 0..20) {
            val offset = (i * 50f + time) % height
            canvas.drawLine(0f, offset, width.toFloat(), offset, paint)
        }
    }

    private fun drawHUD(canvas: Canvas) {
        val topMargin = 50f

        // Draw score
        textPaint.textSize = 50f
        textPaint.color = Color.WHITE
        canvas.drawText("Score: ${gameEngine.getScore()}", centerX, topMargin, textPaint)

        // Draw time
        textPaint.textSize = 40f
        val timeColor = if (gameEngine.getTimeRemaining() <= 10) Color.RED else Color.YELLOW
        textPaint.color = timeColor
        canvas.drawText("Time: ${gameEngine.getTimeRemaining()}s", centerX, topMargin + 60f, textPaint)

        // Draw combo
        if (gameEngine.getComboMultiplier() > 1f) {
            textPaint.textSize = 35f
            textPaint.color = Color.rgb(255, 165, 0)
            canvas.drawText(
                "Combo: ${String.format("%.1f", gameEngine.getComboMultiplier())}x",
                centerX,
                topMargin + 110f,
                textPaint
            )
        }
    }

    private fun drawCores(canvas: Canvas) {
        val bottomMargin = height - 120f
        val cores = coreManager.getUnlockedCores()
        val spacing = width.toFloat() / (cores.size + 1)

        cores.forEachIndexed { index, core ->
            val x = spacing * (index + 1)
            val y = bottomMargin

            // Draw core circle
            val radius = 40f
            paint.style = Paint.Style.FILL

            // Draw energy fill
            val energyRect = RectF(x - radius, y - radius, x + radius, y + radius)
            val sweepAngle = 360f * core.getEnergyPercentage()

            paint.color = Color.argb(100, 50, 50, 50)
            canvas.drawCircle(x, y, radius, paint)

            paint.color = core.color.colorValue
            canvas.drawArc(energyRect, -90f, sweepAngle, true, paint)

            // Draw border
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.color = if (core.isCharged) Color.WHITE else core.color.getDarkerShade()
            canvas.drawCircle(x, y, radius, paint)

            // Draw level indicator
            textPaint.textSize = 20f
            textPaint.color = Color.WHITE
            canvas.drawText("L${core.level}", x, y + radius + 25f, textPaint)

            // Draw glow if charged
            if (core.isCharged) {
                paint.style = Paint.Style.FILL
                paint.color = Color.argb(50, 255, 255, 255)
                canvas.drawCircle(x, y, radius * 1.3f, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameEngine.isGameOver()) return true

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val node = hexGrid.findNodeAtPosition(event.x, event.y)
                if (node != null) {
                    val selected = gameEngine.selectNode(node)
                    if (selected) {
                        soundManager.playSelectSound()
                        // Create trail particle
                        particleSystem.createTrail(node.x, node.y, node.color.colorValue)
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                // Check if tapped on a core
                val bottomMargin = height - 120f
                val cores = coreManager.getUnlockedCores()
                val spacing = width.toFloat() / (cores.size + 1)

                cores.forEachIndexed { index, core ->
                    val coreX = spacing * (index + 1)
                    val coreY = bottomMargin
                    val dx = event.x - coreX
                    val dy = event.y - coreY
                    val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                    if (distance <= 50f && core.isCharged) {
                        gameEngine.useCoreAbility(core.color)
                        soundManager.playAbilitySound()
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    fun cleanup() {
        hexGrid.cleanup()
        particleSystem.clear()
        soundManager.release()
    }
}
