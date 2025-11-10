package com.nexuspaths.game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nexuspaths.game.R

/**
 * Fragment displaying comprehensive game instructions and tutorial
 */
class HowToPlayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_how_to_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button to return to main menu
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
