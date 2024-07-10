package id.andra.knowmyface.view.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import id.andra.knowmyface.databinding.ActivityProfileBinding
import id.andra.knowmyface.helper.Constant
import id.andra.knowmyface.helper.SharedPreferenceHelper
import id.andra.knowmyface.model.User

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        setContentView(binding.root)
        observeState()
        setEventListeners()
        viewModel.setUser(SharedPreferenceHelper.getUser(this) ?: User())
    }

    private fun observeState() {
        viewModel.user.observe(this) { value ->
            if (value != null)
                Picasso.get()
                    .load("${Constant.APIENDPOINT}${value.profileImage}")
                    .into(binding.imgProfile)
        }
    }

    private fun setEventListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

}