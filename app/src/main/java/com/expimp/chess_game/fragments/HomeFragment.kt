package com.expimp.chess_game.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.expimp.chess_game.R


/**
 * Created by Jummania on 13/4/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun startFragment(fragment: Fragment) {
            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null).commit()
        }

        val playGameButton = view.findViewById<Button>(R.id.playGame)
        playGameButton.setOnClickListener {
            startFragment(GameFragment())
        }

        val settingsButton = view.findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener {
            startFragment(SettingsFragment())
        }
    }
}