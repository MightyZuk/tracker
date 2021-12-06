package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import com.example.admin.databinding.ActivityMainPageBinding

class MainPage : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>admin</font>")
        val binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addEmployee.setOnClickListener(this)
        binding.employeeDetails.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addEmployee -> {
                startActivity(Intent(this,AddEmployee::class.java))
            }
            R.id.employeeDetails -> {
                startActivity(Intent(this,EmployeeDetails::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}