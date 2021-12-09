package com.example.admin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.core.app.NavUtils
import com.example.admin.databinding.ActivityEmployeeGeneratedDetailsBinding

class EmployeeGeneratedDetails : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeGeneratedDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeGeneratedDetailsBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        setContentView(binding.root)

        val image = intent.extras?.getParcelable<Bitmap>("image")
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("number")
        val id = intent.getIntExtra("id",0)
        val password = intent.getIntExtra("password",0)

        binding.names.text = name.toString()
        binding.number.text = number.toString()
        binding.id.text = "id: $id"
        binding.password.text = "password: $password"
        binding.employeeImage.setImageBitmap(image)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }
}