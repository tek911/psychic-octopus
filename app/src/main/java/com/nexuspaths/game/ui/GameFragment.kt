package com.nexuspaths.game.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nexuspaths.game.MainActivity
import com.nexuspaths.game.R
import com.nexuspaths.game.game.GameView
import com.nexuspaths.game.models.NodeColor
import com.nexuspaths.game.progression.SaveManager

/**
 * Game fragment that contains the game view
 */
class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var saveManager: SaveManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveManager = SaveManager(requireContext())
        gameView = view.findViewById(R.id.gameView)

        // Load saved cores
        val savedCores = saveManager.loadCores()
        val coreManager = gameView.getCoreManager()
        savedCores.forEach { savedCore ->
            val core = coreManager.getCoreByColor(savedCore.color)
            core?.apply {
                isUnlocked = savedCore.isUnlocked
                level = savedCore.level
            }
        }

        // Set up game over callback
        gameView.onGameOver = { score, energyCollected ->
            showGameOverDialog(score, energyCollected)
        }

        // Pause button
        view.findViewById<Button>(R.id.btnPause).setOnClickListener {
            showPauseDialog()
        }
    }

    private fun showGameOverDialog(finalScore: Int, energyCollected: Map<NodeColor, Int>) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_game_over, null)

        val isNewHighScore = saveManager.saveHighScore(finalScore)
        val shardsEarned = finalScore / 10

        saveManager.addNexusShards(shardsEarned)
        saveManager.incrementGamesPlayed()
        saveManager.saveCores(gameView.getCoreManager().getAllCores())

        // Update achievements
        val achievements = saveManager.loadAchievements()
        achievements.find { it.id == "first_match" }?.updateProgress(1)
        achievements.find { it.id == "10_games" }?.updateProgress(1)
        if (finalScore >= 1000) {
            achievements.find { it.id == "score_1000" }?.updateProgress(finalScore)
        }
        if (finalScore >= 5000) {
            achievements.find { it.id == "score_5000" }?.updateProgress(finalScore)
        }
        if (finalScore >= 10000) {
            achievements.find { it.id == "score_10000" }?.updateProgress(finalScore)
        }
        saveManager.saveAchievements(achievements)

        dialogView.findViewById<TextView>(R.id.tvFinalScore).text = "Score: $finalScore"
        dialogView.findViewById<TextView>(R.id.tvShardsEarned).text = "Shards Earned: $shardsEarned"

        if (isNewHighScore) {
            dialogView.findViewById<TextView>(R.id.tvNewHighScore).visibility = View.VISIBLE
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnPlayAgain).setOnClickListener {
            dialog.dismiss()
            gameView.startGame()
        }

        dialogView.findViewById<Button>(R.id.btnMainMenu).setOnClickListener {
            dialog.dismiss()
            (activity as? MainActivity)?.showFragment(MenuFragment())
        }

        dialog.show()
    }

    private fun showPauseDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Paused")
            .setMessage("Game paused")
            .setPositiveButton("Resume") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Main Menu") { dialog, _ ->
                dialog.dismiss()
                (activity as? MainActivity)?.showFragment(MenuFragment())
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameView.cleanup()
    }
}
