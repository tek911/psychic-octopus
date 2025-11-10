package com.nexuspaths.game.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexuspaths.game.R
import com.nexuspaths.game.progression.Achievement
import com.nexuspaths.game.progression.SaveManager

/**
 * Fragment displaying achievements
 */
class AchievementsFragment : Fragment() {

    private lateinit var saveManager: SaveManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveManager = SaveManager(requireContext())
        val achievements = saveManager.loadAchievements()

        // Setup recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerAchievements)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AchievementAdapter(achievements)

        // Back button
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressed()
        }
    }
}

class AchievementAdapter(
    private val achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvAchievementTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvAchievementDesc)
        val tvProgress: TextView = view.findViewById(R.id.tvProgress)
        val tvReward: TextView = view.findViewById(R.id.tvReward)
        val tvUnlocked: TextView = view.findViewById(R.id.tvUnlocked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.tvTitle.text = achievement.title
        holder.tvDesc.text = achievement.description
        holder.tvProgress.text = "Progress: ${achievement.progress}/${achievement.requirement}"
        holder.tvReward.text = "Reward: ${achievement.rewardShards} shards"

        if (achievement.isUnlocked) {
            holder.tvUnlocked.visibility = View.VISIBLE
            holder.itemView.alpha = 1f
        } else {
            holder.tvUnlocked.visibility = View.GONE
            holder.itemView.alpha = 0.6f
        }
    }

    override fun getItemCount() = achievements.size
}
