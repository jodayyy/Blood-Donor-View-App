package com.example.blooddonorviewapp.repository

import androidx.lifecycle.LiveData
import com.example.blooddonorviewapp.database.Donor
import com.example.blooddonorviewapp.database.DonorDao

class DonorRepository(private val donorDao: DonorDao) {

    fun getAllDonorsLiveData(): LiveData<List<Donor>> = donorDao.getAllDonors()

    suspend fun insertDonor(donor: Donor) = donorDao.insert(donor)

    suspend fun updateDonor(donor: Donor) = donorDao.update(donor)

    suspend fun deleteDonorById(id: String) = donorDao.deleteById(id)

    suspend fun isIdNumberUnique(idNumber: String): Boolean {
        return donorDao.getDonorByIdNumber(idNumber) == null
    }

    // New function to get a donor by their donorId
    suspend fun getDonorById(donorId: String): Donor? {
        return donorDao.getDonorById(donorId)
    }
}
