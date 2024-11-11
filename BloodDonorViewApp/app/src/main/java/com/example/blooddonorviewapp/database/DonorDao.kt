package com.example.blooddonorviewapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface DonorDao {

    @Query("SELECT * FROM Donor")
    fun getAllDonors(): LiveData<List<Donor>>

    @Insert
    suspend fun insert(donor: Donor)

    @Update
    suspend fun update(donor: Donor)

    @Query("DELETE FROM Donor WHERE donorId = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM Donor WHERE idNumber = :idNumber LIMIT 1")
    suspend fun getDonorByIdNumber(idNumber: String): Donor?

    // New query to get a donor by their donorId
    @Query("SELECT * FROM Donor WHERE donorId = :donorId LIMIT 1")
    suspend fun getDonorById(donorId: String): Donor?
}
