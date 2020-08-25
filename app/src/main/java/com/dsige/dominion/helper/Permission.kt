package com.dsige.dominion.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object Permission {

    val PERMISSION_ALL = 1
    val PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )

    val CAMERA_REQUEST = 1
    val GALERY_REQUEST = 2
    val SPEECH_REQUEST = 3
    val REGISTRO_REQUEST = 4
    val UPDATE_REGISTRO_REQUEST = 5
    val CANCEL_REGISTOR_REQUEST = 6

    val POLICY_REQUEST = 7


    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


}