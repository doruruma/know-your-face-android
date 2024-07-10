package id.andra.knowmyface.view.presenceHistory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.andra.knowmyface.databinding.ActivityPresenceHistoryBinding
import id.andra.knowmyface.view.adapter.PresenceAdapter

class PresenceHistoryActivity : AppCompatActivity() {

    private val viewModel: PresenceHistoryViewModel by viewModels()
    private var adapter: PresenceAdapter? = null
    private lateinit var binding: ActivityPresenceHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresenceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        observeState()
        setEventListeners()
        viewModel.loadData()
    }

    private fun observeState() {
        viewModel.items.observe(this) {
            setAdapter()
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = PresenceAdapter(
                act = this,
                items = viewModel.items.value ?: listOf(),
                onMapClick = { url ->
                    if (intent.resolveActivity(packageManager) != null)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            )
            binding.adapter.layoutManager = LinearLayoutManager(this)
            binding.adapter.adapter = adapter
            return
        }
        adapter?.updateData(viewModel.items.value.orEmpty())
    }

    private fun setEventListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

}