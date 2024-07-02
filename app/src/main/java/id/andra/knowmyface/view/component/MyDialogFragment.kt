package id.andra.knowmyface.view.component

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import id.andra.knowmyface.R

open class MyDialogFragment(
    private var width: Double = 0.0,
    private var height: Double = 0.0,
    private var fullHeight: Boolean = false,
    private var fullWidth: Boolean = false,
    private var wrapHeight: Boolean = false,
    private var wrapWidth: Boolean = false
) : DialogFragment() {

    override fun onStart() {
        super.onStart()
        if (fullHeight && fullWidth)
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        else {
            val size = Point()
            val display = dialog?.window?.windowManager?.defaultDisplay
            if (display != null) {
                display.getRealSize(size)
                val displayWidth = size.x
                val displayHeight = size.y
                val trueHeight: Int = if (wrapHeight)
                    WindowManager.LayoutParams.WRAP_CONTENT
                else
                    (displayHeight * height).toInt()
                val trueWidth: Int = if (wrapWidth)
                    WindowManager.LayoutParams.WRAP_CONTENT;
                else
                    (displayWidth * width).toInt()
                dialog?.window?.let {
                    it.setLayout(trueWidth, trueHeight)
                    it.setGravity(Gravity.CENTER)
                }
            }
        }
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}