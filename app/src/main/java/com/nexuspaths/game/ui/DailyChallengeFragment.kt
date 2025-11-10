package com.nexuspaths.game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nexuspaths.game.R
import com.nexuspaths.game.progression.SaveManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Fragment for daily challenge mode
 */
class DailyChallengeFragment : Fragment() {

    private lateinit var saveManager: SaveManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // For now, use a simple text layout
        val view = layoutInflater.inflate(R.layout.fragment_menu, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveManager = SaveManager(requireContext())

        // Check if daily challenge is available
        val lastDaily = saveManager.getLastDailyChallengeDate()
        val currentTime = System.currentTimeMillis()
        val daysSince = TimeUnit.MILLISECONDS.toDays(currentTime - lastDaily)

        val textView = TextView(requireContext()).apply {
            text = if (daysSince >= 1 || lastDaily == 0L) {
                "Daily Challenge Available!\n\nToday's Modifiers:\n" +
                "• Extra Wildcards\n" +
                "• 90 Second Timer\n" +
                "• 2x Score Multiplier\n\n" +
                "Reward: 500 Shards"
            } else {
                "Come back tomorrow for a new daily challenge!"
            }
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(32, 32, 32, 32)
        }

        (view as? ViewGroup)?.removeAllViews()
        (view as? ViewGroup)?.addView(textView)

        val button = Button(requireContext()).apply {
            text = "Back"
            setOnClickListener { activity?.onBackPressed() }
        }
        (view as? ViewGroup)?.addView(button)
    }
}
