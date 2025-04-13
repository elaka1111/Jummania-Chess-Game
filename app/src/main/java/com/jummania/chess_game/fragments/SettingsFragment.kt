package com.jummania.chess_game.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jummania.chess_game.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val symbolStylePreference = findPreference<ListPreference>("symbolStyle")

        val symbolStyle = preferenceManager.sharedPreferences?.getString("symbolStyle", "1")

        symbolStylePreference?.summary = getSymbolStyleName(requireContext(), symbolStyle ?: "1")

        val version = preferenceScreen.findPreference<Preference>("version")
        version?.title = String.format("এপটির সংস্করণঃ %s", getString(R.string.versionName))
    }

    private fun getSymbolStyleName(context: Context, value: String): String? {
        val names = context.resources.getStringArray(R.array.symbol_style_names)
        val values = context.resources.getStringArray(R.array.symbol_style_values)

        val index = values.indexOf(value)
        return if (index != -1) names[index] else null
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "clear" -> {
                preferenceManager.sharedPreferences?.edit {
                    clear()
                }
                activity?.recreate()
            }

            "website" -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, "https://apps.jummania.com".toUri()))
                } catch (e: Exception) {
                    Toast.makeText(
                        context, "No app found to handle this request.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}