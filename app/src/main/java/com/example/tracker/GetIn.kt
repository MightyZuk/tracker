package com.example.tracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tracker.databinding.ActivityGetInBinding

class GetIn : AppCompatActivity() {

    private lateinit var binding: ActivityGetInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGetInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getIn.setOnClickListener{
            val id = binding.uniqueId.text
            val password = binding.password.text
            when{
                id.isEmpty() -> {
                    binding.uniqueId.requestFocus()
                    binding.uniqueId.error = "Required"
                }
                password.isEmpty() -> {
                    binding.password.requestFocus()
                    binding.password.error = "Required"
                }
                id.length < 6 -> {
                    binding.uniqueId.requestFocus()
                    binding.uniqueId.error = "Please enter valid id"
                }
                password.length < 6 -> {
                    binding.password.requestFocus()
                    binding.password.error = "Please enter valid password"
                }
                else -> {
                    startActivity(Intent(this,Employee::class.java))
                }
            }
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}