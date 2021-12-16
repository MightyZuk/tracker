package com.example.tracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
                    checkLoginInfo()
                }
            }
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    private fun checkLoginInfo(){
        val url = "http://192.168.1.7/Employee/getIn.php"

        val request = StringRequest(Request.Method.GET,url,
            {
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,Employee::class.java))
            },
            {Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show()})

        Volley.newRequestQueue(this).add(request)
    }
}