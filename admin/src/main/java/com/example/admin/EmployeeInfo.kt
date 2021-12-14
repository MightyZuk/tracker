package com.example.admin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityEmployeeInfoBinding
import org.json.JSONArray
import java.lang.reflect.Method

class EmployeeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeInfoBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee Details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityEmployeeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientAdapter = ClientListAdapter(this)
        binding.clientList.setHasFixedSize(true)
        binding.clientList.adapter = clientAdapter

        binding.employeeId.text = "id: ${intent.getIntExtra("id",0)}"
        binding.employeeName.text = "${intent.getStringExtra("name")}"
        Glide.with(this).asBitmap().load(intent.getStringExtra("image")).into(binding.employeeImage)

        binding.refreshClient.setOnRefreshListener {
            binding.refreshClient.isRefreshing = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

}