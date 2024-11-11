package com.example.blooddonorviewapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blooddonorviewapp.adapter.DonationEventAdapter
import com.example.blooddonorviewapp.database.AppDatabase
import com.example.blooddonorviewapp.database.DonationEvent
import com.example.blooddonorviewapp.databinding.ActivityDonationEventsBinding
import com.example.blooddonorviewapp.repository.DonationEventRepository
import com.example.blooddonorviewapp.viewmodel.DonationEventViewModel
import com.example.blooddonorviewapp.viewmodel.DonationEventViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DonationEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonationEventsBinding
    private lateinit var viewModel: DonationEventViewModel
    private lateinit var eventAdapter: DonationEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel and Repository with Factory
        val donationEventDao = AppDatabase.getInstance(application).donationEventDao
        val repository = DonationEventRepository(donationEventDao)
        val factory = DonationEventViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(DonationEventViewModel::class.java)

        // Initialize RecyclerView with DonationEventAdapter
        eventAdapter = DonationEventAdapter(
            events = emptyList(),
            onEditClick = { event -> showEditDonationEventDialog(event) },
            onDeleteClick = { event -> showDeleteConfirmationDialog(event) }
        )

        binding.recyclerViewDonationEvents.apply {
            layoutManager = LinearLayoutManager(this@DonationEventsActivity)
            adapter = eventAdapter
        }

        // Observe LiveData of all donation events and update the RecyclerView
        viewModel.getAllDonationEvents().observe(this) { events ->
            eventAdapter.updateData(events)
        }

        // Set up Floating Action Button to open the Add Donation Event dialog
        binding.fabAddDonationEvent.setOnClickListener {
            showAddDonationEventDialog()
        }
    }

    private fun showAddDonationEventDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_donation_event, null)
        val donationDate = dialogView.findViewById<EditText>(R.id.editDonationDate)
        val startTime = dialogView.findViewById<EditText>(R.id.editStartTime)
        val endTime = dialogView.findViewById<EditText>(R.id.editEndTime)
        val donationVenue = dialogView.findViewById<EditText>(R.id.editDonationVenue)
        val organizer = dialogView.findViewById<EditText>(R.id.editOrganizer)
        val target = dialogView.findViewById<EditText>(R.id.editTarget)
        val venueState = dialogView.findViewById<EditText>(R.id.editVenueState)
        val venueCity = dialogView.findViewById<EditText>(R.id.editVenueCity)

        // Set up Date and Time Pickers
        donationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    donationDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        startTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    startTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        endTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    endTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add Donation Event")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val event = DonationEvent(
                    donatDate = donationDate.text.toString(),
                    donatTime = "${startTime.text} - ${endTime.text}",
                    donatVenue = donationVenue.text.toString(),
                    organizer = organizer.text.toString(),
                    target = target.text.toString().toIntOrNull() ?: 0,
                    venueState = venueState.text.toString(),
                    venueCity = venueCity.text.toString()
                )

                // Use CoroutineScope to call the suspend function
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.addDonationEvent(event)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@DonationEventsActivity, "Donation Event Added", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDonationEventDialog(event: DonationEvent) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_donation_event, null)
        val donationDate = dialogView.findViewById<EditText>(R.id.editDonationDate)
        val startTime = dialogView.findViewById<EditText>(R.id.editStartTime)
        val endTime = dialogView.findViewById<EditText>(R.id.editEndTime)
        val donationVenue = dialogView.findViewById<EditText>(R.id.editDonationVenue)
        val organizer = dialogView.findViewById<EditText>(R.id.editOrganizer)
        val target = dialogView.findViewById<EditText>(R.id.editTarget)
        val venueState = dialogView.findViewById<EditText>(R.id.editVenueState)
        val venueCity = dialogView.findViewById<EditText>(R.id.editVenueCity)

        // Pre-fill existing data
        donationDate.setText(event.donatDate)
        startTime.setText(event.donatTime.split(" - ")[0])
        endTime.setText(event.donatTime.split(" - ")[1])
        donationVenue.setText(event.donatVenue)
        organizer.setText(event.organizer)
        target.setText(event.target.toString())
        venueState.setText(event.venueState)
        venueCity.setText(event.venueCity)

        AlertDialog.Builder(this)
            .setTitle("Edit Donation Event")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val updatedEvent = event.copy(
                    donatDate = donationDate.text.toString(),
                    donatTime = "${startTime.text} - ${endTime.text}",
                    donatVenue = donationVenue.text.toString(),
                    organizer = organizer.text.toString(),
                    target = target.text.toString().toIntOrNull() ?: 0,
                    venueState = venueState.text.toString(),
                    venueCity = venueCity.text.toString()
                )

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateDonationEvent(updatedEvent)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@DonationEventsActivity, "Donation Event Updated", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(event: DonationEvent) {
        AlertDialog.Builder(this)
            .setTitle("Delete Donation Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { dialog, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteDonationEvent(event)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@DonationEventsActivity, "Donation Event Deleted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
