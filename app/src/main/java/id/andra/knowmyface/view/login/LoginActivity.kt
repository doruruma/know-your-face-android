package id.andra.knowmyface.view.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import id.andra.knowmyface.view.component.LoadingDialog
import id.andra.knowmyface.view.component.alertDialog.MyAlertDialog
import id.andra.knowmyface.databinding.ActivityLoginBinding
import id.andra.knowmyface.helper.SharedPreferenceHelper
import id.andra.knowmyface.model.User
import id.andra.knowmyface.view.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var loadingDialog: LoadingDialog? = null
    private var alertDialog: MyAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)
        setEventListeners()
        observeState()
    }

    private fun observeState() {
        viewModel.isLoading.observe(this) { value ->
            toggleLoadingDialog(value)
        }
        viewModel.showErrorDialog.observe(this) { value ->
            toggleErrorDialog(value)
        }
        viewModel.loginResponse.observe(this) { value ->
            if (value != null) {
                SharedPreferenceHelper.saveToken(this, value.token)
                SharedPreferenceHelper.saveRefreshToken(this, value.refreshToken)
                viewModel.getCurrentUser()
            }
        }
        viewModel.currentUserResponse.observe(this) { value ->
            if (value != null) {
                SharedPreferenceHelper.saveUser(this, value.data ?: User())
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun toggleLoadingDialog(isShow: Boolean) {
        if (isShow) {
            loadingDialog = LoadingDialog()
            loadingDialog?.show(supportFragmentManager, "LOADING_DIALOG")
            return
        }
        loadingDialog?.dismiss()
    }

    private fun toggleErrorDialog(isShow: Boolean) {
        if (isShow) {
            alertDialog = MyAlertDialog.getInstance(
                title = "Kesalahan",
                message = viewModel.errorMessage.value.orEmpty(),
                onSubmit = {
                    viewModel.toggleShowErrorDialog(false)
                }
            )
            alertDialog?.show(supportFragmentManager, "ALERT_DIALOG")
            return
        }
        alertDialog?.dismiss()
    }

    private fun setEventListeners() {
        binding.inputEmail.addTextChangedListener { text ->
            viewModel.onEmailChanged(text.toString())
        }
        binding.inputPassword.addTextChangedListener { text ->
            viewModel.onPasswordChanged(text.toString())
        }
        binding.btnSubmit.setOnClickListener {
            viewModel.login()
        }
    }

}