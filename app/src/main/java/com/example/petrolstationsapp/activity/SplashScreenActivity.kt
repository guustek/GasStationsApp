package com.example.petrolstationsapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.example.petrolstationsapp.databinding.ActivitySplashScreenBinding
import com.example.petrolstationsapp.utils.LocationService


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : DarkLightModeActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    companion object {
        var permissionDialogShown: Boolean = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)
        if (!permissionDialogShown && LocationService.permissionsGranted(
                this,
                LocationService.ON_START_PERMISSIONS_CODE
            )
        ) {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationService.ON_START_PERMISSIONS_CODE) {
            permissionDialogShown=false
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            } else {
                LocationService.handleDenyLocationPermissions(requestCode, binding.root, this)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!permissionDialogShown && LocationService.permissionsGranted(
                this,
                LocationService.ON_START_PERMISSIONS_CODE
            )
        ) {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

}