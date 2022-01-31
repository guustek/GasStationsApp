package com.example.petrolstationsapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.example.petrolstationsapp.R
import com.example.petrolstationsapp.fragment.SettingsFragment


class SettingsActivity : DarkLightModeActivity(),SharedPreferences.OnSharedPreferenceChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title=getString(R.string.settings)
    }

    override fun onResume() {
        super.onResume()
       preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if(p1=="darkMode") {
            finish()
            startActivity(Intent(this, javaClass))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}