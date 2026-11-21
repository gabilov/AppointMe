package com.beu.pointmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beu.pointmate.R
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.AppointmentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying appointments in a RecyclerView.
 */
class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val dateText: TextView = view.findViewById(R.id.textDate)
        val timeText: TextView = view.findViewById(R.id.textTime)
        val locationText: TextView = view.findViewById(R.id.textLocation)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val cardView: View = view.findViewById(R.id.cardAppointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        val context = holder.itemView.context

        holder.titleText.text = appointment.title
        holder.dateText.text = dateFormat.format(Date(appointment.dateTimeMillis))
        holder.timeText.text = timeFormat.format(Date(appointment.dateTimeMillis))

        if (appointment.location.isNullOrBlank()) {
            holder.locationText.visibility = View.GONE
        } else {
            holder.locationText.visibility = View.VISIBLE
            holder.locationText.text = "📍 ${appointment.location}"
        }

        // Set status
        val now = System.currentTimeMillis()
        if (appointment.status == AppointmentStatus.UPCOMING && appointment.dateTimeMillis < now) {
            holder.statusText.text = context.getString(R.string.status_late)
            holder.statusText.setBackgroundResource(R.drawable.bg_status_late)
            holder.statusText.setTextColor(context.getColor(R.color.status_late))
        } else {
            when (appointment.status) {
                AppointmentStatus.UPCOMING -> {
                    holder.statusText.text = context.getString(R.string.status_upcoming)
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_upcoming)
                    holder.statusText.setTextColor(context.getColor(R.color.status_upcoming))
                }
                AppointmentStatus.COMPLETED -> {
                    holder.statusText.text = context.getString(R.string.status_completed)
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_completed)
                    holder.statusText.setTextColor(context.getColor(R.color.status_completed))
                }
                AppointmentStatus.CANCELLED -> {
                    holder.statusText.text = context.getString(R.string.status_cancelled)
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_cancelled)
                    holder.statusText.setTextColor(context.getColor(R.color.status_cancelled))
                }
            }
        }

        holder.cardView.setOnClickListener {
            onItemClick(appointment)
        }
    }

    override fun getItemCount() = appointments.size

    /**
     * Updates the adapter with new data.
     */
    fun updateData(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}
