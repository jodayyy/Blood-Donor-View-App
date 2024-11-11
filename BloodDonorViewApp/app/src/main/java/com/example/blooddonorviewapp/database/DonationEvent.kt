package com.example.blooddonorviewapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_events")
data class DonationEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val donatDate: String,
    val donatTime: String,
    val donatVenue: String,
    val organizer: String,
    val target: Int,
    val venueState: String,
    val venueCity: String
)
