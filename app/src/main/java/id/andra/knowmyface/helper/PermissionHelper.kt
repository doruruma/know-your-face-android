package id.andra.knowmyface.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(act: Activity) {
        ActivityCompat.requestPermissions(
            act,
            arrayOf(Manifest.permission.CAMERA),
            Constant.CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    fun requestCameraPermissionCallback(
        requestCode: Int,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == Constant.CAMERA_PERMISSION_REQUEST_CODE) {
            return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(act: Activity, callBack: () -> Unit = {}) {
        ActivityCompat.requestPermissions(
            act,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constant.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

}