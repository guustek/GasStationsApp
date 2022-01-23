package com.example.petrolstationsapp.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.petrolstationsapp.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}