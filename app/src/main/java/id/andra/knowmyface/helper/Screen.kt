package id.andra.knowmyface.helper

import android.app.Activity
import android.view.Surface

object Screen {

    fun getScreenOrientation(act: Activity): Int {
        return when (act.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> {
                270
            }

            Surface.ROTATION_180 -> {
                180
            }

            Surface.ROTATION_90 -> {
                90
            }

            else -> {
                0
            }
        }
    }

}