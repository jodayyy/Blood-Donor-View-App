package com.example.blooddonorviewapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blooddonorviewapp.repository.DonorRepository

class DonorViewModelFactory(private val repository: DonorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DonorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DonorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}