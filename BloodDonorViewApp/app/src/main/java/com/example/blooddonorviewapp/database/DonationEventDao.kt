package com.example.blooddonorviewapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DonationEventDao {

    @Insert
    fun insertDonationEvent(event: DonationEvent): Long

    @Update
    fun updateDonationEvent(event: DonationEvent)

    @Delete
    fun deleteDonationEvent(event: DonationEvent)

    @Query("SELECT * FROM donation_events")
    fun getAllDonationEvents(): LiveData<List<DonationEvent>>

    // Query to get unique cities
    @Query("SELECT DISTINCT venueCity FROM donation_events")
    fun getUniqueCities(): LiveData<List<String>>

    // Query to get events by city
    @Query("SELECT * FROM donation_events WHERE venueCity = :city")
    fun getDonationEventsByCity(city: String): LiveData<List<DonationEvent>>

    // Query to get events by date
    @Query("SELECT * FROM donation_events WHERE donatDate = :date")
    fun getDonationEventsByDate(date: String): LiveData<List<DonationEvent>>

    // Query to get events by date and city
    @Query("SELECT * FROM donation_events WHERE donatDate = :date AND venueCity = :city")
    fun getDonationEventsByDateAndCity(date: String, city: String): LiveData<List<DonationEvent>>
}
