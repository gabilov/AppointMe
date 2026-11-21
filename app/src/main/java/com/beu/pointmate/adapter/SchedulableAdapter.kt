package com.beu.pointmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beu.pointmate.R
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.Schedulable
import com.beu.pointmate.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying Schedulable items (Appointments and Tasks) in a RecyclerView.
 * Demonstrates polymorphism by handling different types through a common interface.
 */
class SchedulableAdapter(
    private var items: List<Schedulable>,
    private val onItemClick: (Schedulable) -> Unit
) : RecyclerView.Adapter<SchedulableAdapter.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val timeText: TextView = view.findViewById(R.id.textTime)
        val typeText: TextView = view.findViewById(R.id.textType)
        val cardView: View = view.findViewById(R.id.cardSchedulable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedulable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.titleText.text = item.title
        holder.timeText.text = timeFormat.format(Date(item.dateTimeMillis))

        // Polymorphic behavior - different display based on actual type
        when (item) {
            is Appointment -> {
                holder.typeText.text = "📅 Appointment"
                holder.typeText.setBackgroundResource(R.drawable.bg_status_upcoming)
                holder.typeText.setTextColor(context.getColor(R.color.status_upcoming))
            }
            is Task -> {
                holder.typeText.text = "✓ Task"
                holder.typeText.setBackgroundResource(R.drawable.bg_status_completed)
                holder.typeText.setTextColor(context.getColor(R.color.accent_purple))
            }
        }

        holder.cardView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size

    /**
     * Updates the adapter with new data.
     */
    fun updateData(newItems: List<Schedulable>) {
        items = newItems
        notifyDataSetChanged()
    }
}

