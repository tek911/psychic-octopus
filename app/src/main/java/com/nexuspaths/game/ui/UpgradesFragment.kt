package com.nexuspaths.game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexuspaths.game.MainActivity
import com.nexuspaths.game.R
import com.nexuspaths.game.progression.CoreManager
import com.nexuspaths.game.progression.NexusCore
import com.nexuspaths.game.progression.SaveManager

/**
 * Fragment for managing core upgrades and unlocks
 */
class UpgradesFragment : Fragment() {

    private lateinit var saveManager: SaveManager
    private lateinit var coreManager: CoreManager
    private var currentShards = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upgrades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveManager = SaveManager(requireContext())
        coreManager = CoreManager()

        // Load saved data
        currentShards = saveManager.loadNexusShards()
        val savedCores = saveManager.loadCores()
        savedCores.forEachIndexed { index, savedCore ->
            coreManager.getAllCores().getOrNull(index)?.apply {
                isUnlocked = savedCore.isUnlocked
                level = savedCore.level
            }
        }

        // Update UI
        view.findViewById<TextView>(R.id.tvShards).text = "Shards: $currentShards"

        // Setup recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerCores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CoreAdapter(coreManager.getAllCores()) { core, action ->
            handleCoreAction(core, action, view)
        }

        // Back button
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun handleCoreAction(core: NexusCore, action: CoreAction, view: View) {
        when (action) {
            CoreAction.UNLOCK -> {
                val cost = NexusCore.getUnlockCost(coreManager.getAllCores().indexOf(core))
                if (currentShards >= cost) {
                    core.isUnlocked = true
                    currentShards -= cost
                    saveManager.saveNexusShards(currentShards)
                    saveManager.saveCores(coreManager.getAllCores())
                    updateUI(view)
                    Toast.makeText(requireContext(), "${core.name} unlocked!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Not enough shards!", Toast.LENGTH_SHORT).show()
                }
            }
            CoreAction.UPGRADE -> {
                val cost = core.getUpgradeCost()
                if (currentShards >= cost) {
                    if (core.upgrade()) {
                        currentShards -= cost
                        saveManager.saveNexusShards(currentShards)
                        saveManager.saveCores(coreManager.getAllCores())
                        updateUI(view)
                        Toast.makeText(requireContext(), "${core.name} upgraded!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Not enough shards!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(view: View) {
        view.findViewById<TextView>(R.id.tvShards).text = "Shards: $currentShards"
        view.findViewById<RecyclerView>(R.id.recyclerCores).adapter?.notifyDataSetChanged()
    }
}

enum class CoreAction {
    UNLOCK,
    UPGRADE
}

class CoreAdapter(
    private val cores: List<NexusCore>,
    private val onAction: (NexusCore, CoreAction) -> Unit
) : RecyclerView.Adapter<CoreAdapter.CoreViewHolder>() {

    class CoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCoreName: TextView = view.findViewById(R.id.tvCoreName)
        val tvAbilityName: TextView = view.findViewById(R.id.tvAbilityName)
        val tvAbilityDesc: TextView = view.findViewById(R.id.tvAbilityDesc)
        val tvLevel: TextView = view.findViewById(R.id.tvLevel)
        val btnAction: Button = view.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_core, parent, false)
        return CoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoreViewHolder, position: Int) {
        val core = cores[position]

        holder.tvCoreName.text = core.name
        holder.tvCoreName.setTextColor(core.color.colorValue)
        holder.tvAbilityName.text = core.abilityType.displayName
        holder.tvAbilityDesc.text = core.abilityType.description
        holder.tvLevel.text = "Level: ${core.level}/3"

        if (!core.isUnlocked) {
            val cost = NexusCore.getUnlockCost(position)
            holder.btnAction.text = "Unlock ($cost shards)"
            holder.btnAction.setOnClickListener {
                onAction(core, CoreAction.UNLOCK)
            }
        } else if (core.level < 3) {
            val cost = core.getUpgradeCost()
            holder.btnAction.text = "Upgrade to L${core.level + 1} ($cost shards)"
            holder.btnAction.setOnClickListener {
                onAction(core, CoreAction.UPGRADE)
            }
        } else {
            holder.btnAction.text = "Max Level"
            holder.btnAction.isEnabled = false
        }
    }

    override fun getItemCount() = cores.size
}
