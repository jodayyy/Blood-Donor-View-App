package com.example.blooddonorviewapp

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blooddonorviewapp.database.AppDatabase
import com.example.blooddonorviewapp.database.Donor
import com.example.blooddonorviewapp.databinding.ActivityUserBinding
import com.example.blooddonorviewapp.repository.DonorRepository
import com.example.blooddonorviewapp.viewmodel.DonorViewModel
import com.example.blooddonorviewapp.viewmodel.DonorViewModelFactory

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var donorViewModel: DonorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val donorDao = AppDatabase.getInstance(application).donorDao
        val repository = DonorRepository(donorDao)
        val factory = DonorViewModelFactory(repository)
        donorViewModel = ViewModelProvider(this, factory).get(DonorViewModel::class.java)

        // Observe donor data and populate the UI with buttons
        donorViewModel.donors.observe(this) { donors ->
            populateDonors(donors)
        }
    }

    // Function to add buttons dynamically for each donor
    private fun populateDonors(donors: List<Donor>) {
        binding.donorsContainer.removeAllViews() // Clear existing buttons if any
        donors.forEach { donor ->
            val button = Button(this).apply {
                text = donor.donorFullName
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    // Launch DonorDetailsActivity with donor information
                    val intent = Intent(this@UserActivity, DonorDetailsActivity::class.java).apply {
                        putExtra("donorId", donor.donorId)
                        putExtra("donorName", donor.donorFullName)
                        putExtra("donorIdNumber", donor.idNumber)
                        putExtra("donorBloodType", donor.donorBloodGroup)
                        putExtra("donationCount", donor.countOfDonations)
                    }
                    startActivity(intent)
                }
            }
            binding.donorsContainer.addView(button)
        }
    }
}
