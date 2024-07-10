package id.andra.knowmyface.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import id.andra.knowmyface.R
import id.andra.knowmyface.databinding.ItemPresenceBinding
import id.andra.knowmyface.helper.Constant
import id.andra.knowmyface.model.Presence

class PresenceAdapter(
    private var act: Activity,
    private var items: List<Presence>,
    private var onMapClick: (mapUrl: String) -> Unit,
) : RecyclerView.Adapter<PresenceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPresenceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresenceBinding.inflate(LayoutInflater.from(act), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]
        val clockOutVisibility =
            if (item.timeOut != null)
                View.VISIBLE
            else
                View.GONE
        binding.imgClockOut.visibility = clockOutVisibility
        binding.btnLookMapClockOut.visibility = clockOutVisibility
        val statusColor =
            if (item.isLate == 1)
                R.color.red_darken_2
            else
                R.color.green
        binding.txtStatus.setTextColor(act.getColor(statusColor))
        binding.txtStatus.text = item.isLateLabel
        binding.txtDate.text = item.createdAt
        binding.txtClockIn.text = item.timeIn
        binding.txtClockOut.text = item.timeOut ?: "-"
        Picasso.get()
            .load("${Constant.APIENDPOINT}${item.faceImageClockIn}")
            .into(binding.imgClockIn)
        if (item.faceImageClockOut != null)
            Picasso.get()
                .load("${Constant.APIENDPOINT}${item.faceImageClockOut}")
                .into(binding.imgClockOut)
        binding.btnLookMapClockIn.setOnClickListener {
            onMapClick.invoke("https://maps.google.com/?q=${item.latitudeClockIn},${item.longitudeClockIn}")
        }
        binding.btnLookMapClockOut.setOnClickListener {
            onMapClick.invoke("https://maps.google.com/?q=${item.latitudeClockOut},${item.longitudeClockOut}")
        }
    }

    fun updateData(items: List<Presence>) {
        this.items = items
        notifyDataSetChanged()
    }

}