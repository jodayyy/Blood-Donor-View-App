package com.example.blooddonorviewapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonorviewapp.databinding.ActivityDatabaseBinding

class DatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatabaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Navigate to DonorsActivity
        binding.buttonDonors.setOnClickListener {
            val intent = Intent(this, DonorsActivity::class.java)
            startActivity(intent)
        }

        // Navigate to DonationEventsActivity
        binding.buttonDonationEvents.setOnClickListener {
            val intent = Intent(this, DonationEventsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
