package com.example.blooddonorviewapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blooddonorviewapp.adapter.DonorAdapter
import com.example.blooddonorviewapp.database.AppDatabase
import com.example.blooddonorviewapp.database.Donor
import com.example.blooddonorviewapp.databinding.ActivityDonorsBinding
import com.example.blooddonorviewapp.repository.DonorRepository
import com.example.blooddonorviewapp.viewmodel.DonorViewModel
import com.example.blooddonorviewapp.viewmodel.DonorViewModelFactory
import java.util.Calendar
import java.util.UUID

class DonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorsBinding
    private lateinit var donorAdapter: DonorAdapter
    private lateinit var donorViewModel: DonorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Repository and ViewModel with Factory using AppDatabase
        val donorDao = AppDatabase.getInstance(application).donorDao
        val repository = DonorRepository(donorDao)
        val factory = DonorViewModelFactory(repository)
        donorViewModel = ViewModelProvider(this, factory).get(DonorViewModel::class.java)

        // Initialize RecyclerView and Adapter
        donorAdapter = DonorAdapter(emptyList(),
            onEditClick = { donor -> showEditDonorDialog(donor) },
            onDeleteClick = { donor -> showDeleteDonorDialog(donor) })

        binding.recyclerViewDonors.apply {
            layoutManager = LinearLayoutManager(this@DonorsActivity)
            adapter = donorAdapter
        }

        // Observe donor data from ViewModel
        donorViewModel.donors.observe(this) { donors ->
            donorAdapter.updateData(donors)
        }

        // Set up FAB to open the Add Donor dialog
        binding.fabAddDonor.setOnClickListener {
            showAddDonorDialog()
        }
    }

    private fun showAddDonorDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_donor, null)
        val donorName = dialogView.findViewById<EditText>(R.id.editDonorName)
        val donorIdNumber = dialogView.findViewById<EditText>(R.id.editDonorIdNumber)
        val donorBloodGroup = dialogView.findViewById<EditText>(R.id.editDonorBloodGroup)
        val lastDonationDate = dialogView.findViewById<EditText>(R.id.editLastDonationDate)

        donorIdNumber.inputType = InputType.TYPE_CLASS_NUMBER

        // Set up DatePicker for Last Donation Date
        lastDonationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    lastDonationDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add Donor")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = donorName.text.toString()
                val idNumber = donorIdNumber.text.toString()
                val bloodGroup = donorBloodGroup.text.toString()
                val donationDate = lastDonationDate.text.toString()

                // Check if ID Number is unique
                donorViewModel.isIdNumberUnique(idNumber) { isUnique ->
                    if (isUnique) {
                        val donor = Donor(
                            donorId = UUID.randomUUID().toString(),
                            donorFullName = name,
                            idNumber = idNumber,
                            donorBloodGroup = bloodGroup,
                            lastDonationDate = donationDate
                        )
                        donorViewModel.addDonor(donor)
                        Toast.makeText(this, "Donor Added", Toast.LENGTH_SHORT).show()
                        dialog.dismiss() // Close dialog on successful add
                    } else {
                        showAlert("ID Number already exists.")
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDonorDialog(donor: Donor) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_donor, null)
        val donorName = dialogView.findViewById<EditText>(R.id.editDonorName)
        val donorIdNumber = dialogView.findViewById<EditText>(R.id.editDonorIdNumber)
        val donorBloodGroup = dialogView.findViewById<EditText>(R.id.editDonorBloodGroup)
        val lastDonationDate = dialogView.findViewById<EditText>(R.id.editLastDonationDate)

        // Pre-fill existing data
        donorName.setText(donor.donorFullName)
        donorIdNumber.setText(donor.idNumber)
        donorBloodGroup.setText(donor.donorBloodGroup)
        lastDonationDate.setText(donor.lastDonationDate)

        // DatePicker for Last Donation Date
        lastDonationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    lastDonationDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Donor")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val updatedDonor = donor.copy(
                    donorFullName = donorName.text.toString(),
                    idNumber = donorIdNumber.text.toString(),
                    donorBloodGroup = donorBloodGroup.text.toString(),
                    lastDonationDate = lastDonationDate.text.toString()
                )
                donorViewModel.updateDonor(updatedDonor)
                dialog.dismiss() // Close dialog on successful update
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDonorDialog(donor: Donor) {
        AlertDialog.Builder(this)
            .setTitle("Delete Donor")
            .setMessage("Are you sure you want to delete this donor?")
            .setPositiveButton("Delete") { dialog, _ ->
                donorViewModel.deleteDonor(donor)
                Toast.makeText(this, "Donor Deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Close dialog on successful delete
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Utility function to show an alert dialog
    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Invalid Input")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
