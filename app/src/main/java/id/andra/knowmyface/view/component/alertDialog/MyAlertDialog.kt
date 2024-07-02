package id.andra.knowmyface.view.component.alertDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import id.andra.knowmyface.R
import id.andra.knowmyface.view.component.MyDialogFragment
import id.andra.knowmyface.databinding.DialogAlertBinding

class MyAlertDialog : MyDialogFragment(
    fullWidth = true,
    fullHeight = true
) {

    companion object {
        lateinit var title: String
        lateinit var message: String
        lateinit var onSubmit: () -> Unit

        @JvmStatic
        fun getInstance(
            title: String,
            message: String,
            onSubmit: () -> Unit
        ): MyAlertDialog {
            Companion.title = title
            Companion.message = message
            Companion.onSubmit = onSubmit
            return MyAlertDialog()
        }
    }

    private lateinit var binding: DialogAlertBinding
    private val viewModel: AlertDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_alert, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        viewModel.setTitle(title)
        viewModel.setMessage(message)
        setEventListeners()
        return binding.root
    }

    private fun setEventListeners() {
        binding.btnSubmit.setOnClickListener {
            onSubmit.invoke()
        }
    }

}