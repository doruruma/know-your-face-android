package id.andra.knowmyface.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.andra.knowmyface.databinding.ActivitySplashBinding
import id.andra.knowmyface.helper.SharedPreferenceHelper
import id.andra.knowmyface.view.login.LoginActivity
import id.andra.knowmyface.view.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        setContentView(binding.root)
        observeState()
        val refreshToken = SharedPreferenceHelper.getRefreshToken(this)
        if (refreshToken == null) {
            openLoginActivity()
        } else {
            viewModel.refreshToken(refreshToken)
        }
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun observeState() {
        viewModel.refreshTokenResponse.observe(this) { value ->
            if (value != null) {
                SharedPreferenceHelper.saveToken(this, value.token)
                SharedPreferenceHelper.saveRefreshToken(this, value.refreshToken)
                openMainActivity()
            }
        }
        viewModel.refreshTokenError.observe(this) { value ->
            if (value) {
                openLoginActivity()
            }
        }
    }

}