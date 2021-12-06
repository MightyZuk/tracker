package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.admin.databinding.ActivityAddEmployeeBinding
import com.example.admin.databinding.ActivityOtpVerificationBinding
import kotlin.random.Random

class OtpVerification : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val id = Random.nextInt(100000,999999)
        val password = Random.nextInt(100000,999999)
        val name = intent.getStringExtra("name")

        binding.verify.setOnClickListener {
            Intent(this,AddEmployee::class.java).also {
                it.putExtra("id",id.toString())
                it.putExtra("password",password.toString())
                it.putExtra("name",name.toString())
                startActivity(it)
            }
        }
    }
}