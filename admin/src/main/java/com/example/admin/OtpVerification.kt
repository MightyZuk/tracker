package com.example.admin

import android.content.Intent
import android.graphics.Bitmap
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
        val number = intent.getStringExtra("number")
        val image = intent.extras?.getParcelable<Bitmap>("image")

        binding.verify.setOnClickListener {
            Intent(this,EmployeeGeneratedDetails::class.java).also {
                it.putExtra("id",id)
                it.putExtra("password",password)
                it.putExtra("name",name.toString())
                it.putExtra("number",number.toString())
                it.putExtra("image",image)
                startActivity(it)
            }
        }
    }
}