package com.example.blooddonorviewapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.blooddonorviewapp.database.AppDatabase
import com.example.blooddonorviewapp.database.Donor
import com.example.blooddonorviewapp.database.DonationEvent
import com.example.blooddonorviewapp.databinding.ActivityDonorDetailsBinding
import com.example.blooddonorviewapp.repository.DonationEventRepository
import com.example.blooddonorviewapp.repository.DonorRepository
import com.example.blooddonorviewapp.viewmodel.DonationEventViewModel
import com.example.blooddonorviewapp.viewmodel.DonationEventViewModelFactory
import com.example.blooddonorviewapp.viewmodel.DonorViewModel
import com.example.blooddonorviewapp.viewmodel.DonorViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DonorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorDetailsBinding
    private lateinit var donorViewModel: DonorViewModel
    private lateinit var donationEventViewModel: DonationEventViewModel
    private lateinit var currentDonor: Donor
    private lateinit var calendarView: CalendarView
    private val eventMap = mutableMapOf<String, List<DonationEvent>>() // Maps date strings to events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModels
        val donorDao = AppDatabase.getInstance(application).donorDao
        val donorRepository = DonorRepository(donorDao)
        val donorFactory = DonorViewModelFactory(donorRepository)
        donorViewModel = ViewModelProvider(this, donorFactory)[DonorViewModel::class.java]

        val donationEventDao = AppDatabase.getInstance(application).donationEventDao
        val donationEventRepository = DonationEventRepository(donationEventDao)
        val donationEventFactory = DonationEventViewModelFactory(donationEventRepository)
        donationEventViewModel = ViewModelProvider(this, donationEventFactory)[DonationEventViewModel::class.java]

        // Retrieve donor details
        val donorId = intent.getStringExtra("donorId") ?: return
        donorViewModel.getDonorById(donorId) { donor ->
            currentDonor = donor ?: return@getDonorById
            displayDonorDetails(donor)
        }

        // Set up calendar and city filtering
        calendarView = binding.calendarView
        setCalendarHeaderColor(Color.parseColor("#FF0000")) // Customize header color

        donationEventViewModel.uniqueCities.observe(this) { cities ->
            setupCitySpinner(cities)
        }

        // Set listener for calendar date clicks
        calendarView.setOnDayClickListener(object : com.applandeo.materialcalendarview.listeners.OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(eventDay.calendar.time)
                eventMap[selectedDate]?.let { events ->
                    showEventDetailsDialog(selectedDate, events)
                }
            }
        })

        // Load initial events for all cities
        loadAllEvents()
    }

    private fun displayDonorDetails(donor: Donor) {
        binding.donorName.text = donor.donorFullName
        binding.donorIdNumber.text = donor.idNumber
        binding.donorBloodType.text = donor.donorBloodGroup
        binding.donationCountBadge.text = donor.countOfDonations.toString()
        binding.lastDonationDate.text = donor.lastDonationDate
        binding.nextEligibleDonationDate.text = calculateNextEligibleDonationDate(donor.lastDonationDate)

        binding.donationCountBadge.setOnClickListener { showNumberPickerDialog() }
        binding.lastDonationDate.setOnClickListener { showDatePickerDialog() }
    }

    private fun setCalendarHeaderColor(color: Int) {
        try {
            val headerView = calendarView.findViewById<View>(com.applandeo.materialcalendarview.R.id.calendarHeader)
            headerView?.setBackgroundColor(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateNextEligibleDonationDate(lastDonationDate: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(lastDonationDate) ?: calendar.time
        calendar.add(Calendar.MONTH, 3)
        return dateFormat.format(calendar.time)
    }

    private fun showNumberPickerDialog() {
        val numberPicker = NumberPicker(this).apply {
            minValue = 0
            maxValue = 100
            value = currentDonor.countOfDonations
        }

        AlertDialog.Builder(this)
            .setTitle("Set Donation Count")
            .setView(numberPicker)
            .setPositiveButton("OK") { dialog, _ ->
                updateDonationCount(numberPicker.value)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            updateLastDonationDate(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDonationCount(newCount: Int) {
        val updatedDonor = currentDonor.copy(countOfDonations = newCount)
        donorViewModel.updateDonor(updatedDonor)
        binding.donationCountBadge.text = newCount.toString()
        Toast.makeText(this, "Donation count updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateLastDonationDate(newDate: String) {
        val updatedDonor = currentDonor.copy(lastDonationDate = newDate)
        donorViewModel.updateDonor(updatedDonor)
        binding.lastDonationDate.text = newDate
        binding.nextEligibleDonationDate.text = calculateNextEligibleDonationDate(newDate)
        Toast.makeText(this, "Last donation date updated", Toast.LENGTH_SHORT).show()
    }

    private fun setupCitySpinner(cities: List<String>) {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = spinnerAdapter

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position) as String
                loadCityEvents(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadAllEvents() {
        donationEventViewModel.getAllDonationEvents().observe(this) { events ->
            updateCalendarEvents(events)
        }
    }

    private fun loadCityEvents(city: String) {
        donationEventViewModel.getAllDonationEvents().observe(this) { events ->
            val filteredEvents = events.filter { it.venueCity == city }
            updateCalendarEvents(filteredEvents)
        }
    }

    private fun updateCalendarEvents(events: List<DonationEvent>) {
        val eventDays = mutableListOf<EventDay>()
        eventMap.clear()
        for (event in events) {
            val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.donatDate)
            val calendar = Calendar.getInstance().apply { time = eventDate }
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(eventDate)
            eventMap[dateStr] = eventMap.getOrDefault(dateStr, emptyList()) + event
            eventDays.add(EventDay(calendar, R.drawable.red_dot)) // Add red dot for events
        }
        calendarView.setEvents(eventDays)
    }

    private fun showEventDetailsDialog(date: String, events: List<DonationEvent>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_event_details)

        val title = dialog.findViewById<TextView>(R.id.eventDateTitle)
        val details = dialog.findViewById<TextView>(R.id.eventDetails)

        title.text = "Events on $date"
        details.text = events.joinToString(separator = "\n\n") { event ->
            "Venue: ${event.donatVenue}\nOrganizer: ${event.organizer}\nTime: ${event.donatTime}\nTarget: ${event.target} Blood Bags"
        }

        dialog.show()
    }
}
