package com.example.blooddonorviewapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blooddonorviewapp.repository.DonationEventRepository

class DonationEventViewModelFactory(private val repository: DonationEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DonationEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DonationEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
