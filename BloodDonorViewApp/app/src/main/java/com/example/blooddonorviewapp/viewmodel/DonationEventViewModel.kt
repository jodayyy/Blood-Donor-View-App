package com.example.blooddonorviewapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.blooddonorviewapp.database.DonationEvent
import com.example.blooddonorviewapp.repository.DonationEventRepository

class DonationEventViewModel(private val repository: DonationEventRepository) : ViewModel() {

    // Expose unique cities as LiveData
    val uniqueCities: LiveData<List<String>> = repository.getUniqueCities()

    // Get all donation events
    fun getAllDonationEvents(): LiveData<List<DonationEvent>> = repository.getAllDonationEventsLiveData()

    // Add a donation event
    suspend fun addDonationEvent(event: DonationEvent) = repository.insertDonationEvent(event)

    // Update a donation event
    suspend fun updateDonationEvent(event: DonationEvent) = repository.updateDonationEvent(event)

    // Delete a donation event
    suspend fun deleteDonationEvent(event: DonationEvent) = repository.deleteDonationEvent(event)

    // Fetch events by city
    fun getEventsByCity(city: String?): LiveData<List<DonationEvent>> = repository.getEventsByCity(city)

    // Fetch events by date and city
    fun getEventsByDateAndCity(date: String, city: String?): LiveData<List<DonationEvent>> =
        repository.getEventsByDateAndCity(date, city)
}
