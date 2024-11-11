package com.example.blooddonorviewapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blooddonorviewapp.database.Donor
import com.example.blooddonorviewapp.repository.DonorRepository
import kotlinx.coroutines.launch

class DonorViewModel(private val repository: DonorRepository) : ViewModel() {

    // LiveData list of all donors
    val donors: LiveData<List<Donor>> = repository.getAllDonorsLiveData()

    // Add a new donor to the database
    fun addDonor(donor: Donor) {
        viewModelScope.launch {
            repository.insertDonor(donor)
        }
    }

    // Update an existing donor's information
    fun updateDonor(donor: Donor) {
        viewModelScope.launch {
            repository.updateDonor(donor)
        }
    }

    // Delete a donor by donor object
    fun deleteDonor(donor: Donor) {
        viewModelScope.launch {
            repository.deleteDonorById(donor.donorId)
        }
    }

    // Check if an ID number is unique
    fun isIdNumberUnique(idNumber: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isUnique = repository.isIdNumberUnique(idNumber)
            callback(isUnique)
        }
    }

    // Get a donor by ID for displaying details
    fun getDonorById(donorId: String, callback: (Donor?) -> Unit) {
        viewModelScope.launch {
            val donor = repository.getDonorById(donorId)
            callback(donor)
        }
    }
}
