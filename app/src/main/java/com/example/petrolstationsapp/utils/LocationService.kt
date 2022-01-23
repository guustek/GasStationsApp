package com.example.petrolstationsapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.petrolstationsapp.activity.MainActivity
import com.example.petrolstationsapp.activity.SplashScreenActivity
import com.google.android.material.snackbar.Snackbar


class LocationService {
    companion object {

        const val ON_START_PERMISSIONS_CODE = 997
        const val ON_MAP_LOAD_PERMISSIONS_CODE = 2137

        fun permissionsGranted(activity: Activity, requestCode: Int): Boolean {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!dialogIsShown(activity))
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                        requestCode
                    )
                return false
            }
            return true
        }

        private fun dialogIsShown(activity: Activity): Boolean {
            if (activity is SplashScreenActivity)
                if (SplashScreenActivity.permissionDialogShown)
                    return true
                else
                    SplashScreenActivity.permissionDialogShown = true
            if (activity is MainActivity)
                if (MainActivity.permissionDialogShown)
                    return true
                else
                    MainActivity.permissionDialogShown = true
            return false
        }


        @RequiresApi(Build.VERSION_CODES.M)
        fun handleDenyLocationPermissions(requestCode: Int, view: View, activity: Activity) {
            if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val snackbar = Snackbar.make(
                    view,
                    "Nie uzyskano zgody na lokalizacje! Aplikacja nie będzie działać",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("Zezwól") {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        requestCode

                    )
                }
                snackbar.show()
            } else {
                val snackbar = Snackbar.make(
                    view,
                    "Nie uzyskano zgody na lokalizacje! Aplikacja nie będzie działać",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("Ustawienia") {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                }
                snackbar.show()
            }
        }


        fun isInternetAvailable(context: Context): Boolean {
            var haveConnectedWifi = false
            var haveConnectedMobile = false

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) haveConnectedWifi = true
                if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) haveConnectedMobile = true
            }
            return haveConnectedWifi || haveConnectedMobile
        }

        fun isConnected(): Boolean {
            val command = "ping -c 1 google.com"
            return Runtime.getRuntime().exec(command).waitFor() == 0
        }
    }
}