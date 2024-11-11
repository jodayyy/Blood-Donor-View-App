package com.example.blooddonorviewapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonorviewapp.R
import com.example.blooddonorviewapp.database.Donor

class DonorAdapter(
    private var donors: List<Donor>,
    private val onEditClick: (Donor) -> Unit,
    private val onDeleteClick: (Donor) -> Unit
) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors[position]
        holder.bind(donor)
        holder.itemView.findViewById<View>(R.id.editButton).setOnClickListener {
            onEditClick(donor)
        }
        holder.itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            onDeleteClick(donor)
        }
    }

    override fun getItemCount(): Int = donors.size

    fun updateData(newDonors: List<Donor>) {
        donors = newDonors
        notifyDataSetChanged()
    }

    class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val donorName: TextView = itemView.findViewById(R.id.donorName)
        private val donorIdNumber: TextView = itemView.findViewById(R.id.donorIdNumber)
        private val donorBloodGroup: TextView = itemView.findViewById(R.id.donorBloodGroup)
        private val donorLastDonationDate: TextView = itemView.findViewById(R.id.donorLastDonationDate)

        fun bind(donor: Donor) {
            donorName.text = donor.donorFullName
            donorIdNumber.text = donor.idNumber
            donorBloodGroup.text = donor.donorBloodGroup
            donorLastDonationDate.text = "Last Donation: ${donor.lastDonationDate}"
        }
    }
}
