package com.example.blooddonorviewapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["idNumber"], unique = true)]
)
data class Donor(
    @PrimaryKey val donorId: String,
    val donorFullName: String,
    val idNumber: String,
    val donorBloodGroup: String,
    val lastDonationDate: String = "", // New field for last donation date
    val countOfDonations: Int = 0
)
