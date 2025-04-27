package com.expimp.chess_game.fragments

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.expimp.chess_game.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jummania.ChessView


/**
 * Created by Jummania on 13/4/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class GameFragment : Fragment(R.layout.fragment_game) {

    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mActivity = activity ?: return

        val chessView = view.findViewById<ChessView>(R.id.chessView)

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(mActivity)

        preferenceManager?.apply {

            val playMusic = getBoolean("playMusic", false)
            if (playMusic) {
                mediaPlayer = MediaPlayer.create(mActivity, R.raw.music)
                playBackground(mediaPlayer!!)
            }

            chessView.setSoundEffectEnabled(getBoolean("clickSound", true))

            chessView.setBackgroundColor(getInt("backgroundColor", Color.WHITE))

            chessView.setPieceStyle(
                getBoolean("isLightFilled", true),
                getBoolean("isDarkFilled", true),
                getInt("pieceLightColor", Color.WHITE),
                getInt("pieceDarkColor", Color.BLACK)
            )

            chessView.setSquareColors(
                getInt("lightSquareColor", "#fadeaf".toColorInt()),
                getInt("darkSquareColor", "#8e4f19".toColorInt())
            )

            chessView.setEnableStroke(
                getBoolean("enableStroke", true),
                getInt("strokeLightColor", Color.BLACK),
                getInt("strokeDarkColor", Color.BLACK)
            )

            chessView.setSymbolStyle(
                getString("symbolStyle", "3")!!.toInt(), getBoolean("isBoldSymbol", false)
            )
        }


        val mWindow = mActivity.window
        WindowInsetsControllerCompat(
            mWindow, mActivity.findViewById(android.R.id.content)
        ).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(mActivity).setTitle("Are you sure you want to exit?")
                .setMessage("Any unsaved progress may be lost.").setPositiveButton("Exit") { _, _ ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, HomeFragment()).addToBackStack(null)
                        .commit()
                }.setNegativeButton("Cancel", null).show()
        }
    }

    private fun playBackground(mediaPlayer: MediaPlayer) {
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.seekTo(mediaPlayer.duration / 2)
            mediaPlayer.start()
        }

        mediaPlayer.start()
    }

    override fun onStop() {
        mediaPlayer?.release()
        super.onStop()
    }
}