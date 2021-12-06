package com.example.tracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import com.example.tracker.databinding.ActivityFormBinding

class Form : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFormBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Form</font>")
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            Toast.makeText(this,"Client Details has been Regirstered",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Employee::class.java))
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}