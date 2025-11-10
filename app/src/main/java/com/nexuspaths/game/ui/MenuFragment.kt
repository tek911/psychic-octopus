package com.nexuspaths.game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nexuspaths.game.MainActivity
import com.nexuspaths.game.R
import com.nexuspaths.game.progression.SaveManager

/**
 * Main menu fragment
 */
class MenuFragment : Fragment() {

    private lateinit var saveManager: SaveManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveManager = SaveManager(requireContext())

        // Update UI
        view.findViewById<TextView>(R.id.tvHighScore).text =
            "High Score: ${saveManager.loadHighScore()}"
        view.findViewById<TextView>(R.id.tvShards).text =
            "Nexus Shards: ${saveManager.loadNexusShards()}"

        // Play button
        view.findViewById<Button>(R.id.btnPlay).setOnClickListener {
            (activity as? MainActivity)?.showFragment(GameFragment(), true)
        }

        // Upgrades button
        view.findViewById<Button>(R.id.btnUpgrades).setOnClickListener {
            (activity as? MainActivity)?.showFragment(UpgradesFragment(), true)
        }

        // Achievements button
        view.findViewById<Button>(R.id.btnAchievements).setOnClickListener {
            (activity as? MainActivity)?.showFragment(AchievementsFragment(), true)
        }

        // Daily challenge button
        view.findViewById<Button>(R.id.btnDailyChallenge).setOnClickListener {
            (activity as? MainActivity)?.showFragment(DailyChallengeFragment(), true)
        }

        // How to Play button
        view.findViewById<Button>(R.id.btnHowToPlay).setOnClickListener {
            (activity as? MainActivity)?.showFragment(HowToPlayFragment(), true)
        }
    }
}
