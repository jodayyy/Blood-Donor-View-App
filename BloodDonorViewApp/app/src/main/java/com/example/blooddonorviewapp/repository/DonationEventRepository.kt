package com.example.blooddonorviewapp.repository

import androidx.lifecycle.LiveData
import com.example.blooddonorviewapp.database.DonationEvent
import com.example.blooddonorviewapp.database.DonationEventDao

class DonationEventRepository(private val donationEventDao: DonationEventDao) {

    // Fetch unique cities
    fun getUniqueCities(): LiveData<List<String>> = donationEventDao.getUniqueCities()

    // Insert a donation event
    suspend fun insertDonationEvent(event: DonationEvent) = donationEventDao.insertDonationEvent(event)

    // Update a donation event
    suspend fun updateDonationEvent(event: DonationEvent) = donationEventDao.updateDonationEvent(event)

    // Delete a donation event
    suspend fun deleteDonationEvent(event: DonationEvent) = donationEventDao.deleteDonationEvent(event)

    // Get all donation events
    fun getAllDonationEventsLiveData(): LiveData<List<DonationEvent>> = donationEventDao.getAllDonationEvents()

    // Fetch events by city
    fun getEventsByCity(city: String?): LiveData<List<DonationEvent>> {
        return if (city.isNullOrEmpty()) {
            donationEventDao.getAllDonationEvents()
        } else {
            donationEventDao.getDonationEventsByCity(city)
        }
    }

    // Fetch events by date and city
    fun getEventsByDateAndCity(date: String, city: String?): LiveData<List<DonationEvent>> {
        return if (city.isNullOrEmpty()) {
            donationEventDao.getDonationEventsByDate(date)
        } else {
            donationEventDao.getDonationEventsByDateAndCity(date, city)
        }
    }
}
