package id.andra.knowmyface.view.recordPresence

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.andra.knowmyface.databinding.ActivityRecordPresenceBinding

class RecordPresenceActivity : AppCompatActivity() {

    private val viewModel: RecordPresenceViewModel by viewModels()
    private lateinit var binding: ActivityRecordPresenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPresenceBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)
    }

}