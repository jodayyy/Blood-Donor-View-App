package com.example.blooddonorviewapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonorviewapp.R
import com.example.blooddonorviewapp.database.DonationEvent

class DonationEventAdapter(
    private var events: List<DonationEvent>,
    private val onEditClick: (DonationEvent) -> Unit,
    private val onDeleteClick: (DonationEvent) -> Unit
) : RecyclerView.Adapter<DonationEventAdapter.DonationEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donation_event, parent, false)
        return DonationEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationEventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.findViewById<View>(R.id.editButton).setOnClickListener {
            onEditClick(event)
        }
        holder.itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            onDeleteClick(event) // Trigger delete function on delete button click
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateData(newEvents: List<DonationEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class DonationEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        private val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        private val eventVenue: TextView = itemView.findViewById(R.id.eventVenue)
        private val eventOrganizer: TextView = itemView.findViewById(R.id.eventOrganizer)
        private val eventTarget: TextView = itemView.findViewById(R.id.eventTarget)

        fun bind(event: DonationEvent) {
            eventDate.text = "Date: ${event.donatDate}"
            eventTime.text = "Time: ${event.donatTime}"
            eventVenue.text = "Venue: ${event.donatVenue}"
            eventOrganizer.text = "Organizer: ${event.organizer}"
            eventTarget.text = "Target: ${event.target}"
        }
    }
}
