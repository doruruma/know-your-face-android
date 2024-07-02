package id.andra.knowmyface.view.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.andra.knowmyface.R

class LoadingDialog : MyDialogFragment(
    fullHeight = true,
    fullWidth = true
) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

}