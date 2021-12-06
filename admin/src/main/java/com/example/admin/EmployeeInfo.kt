package com.example.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.example.admin.databinding.ActivityEmployeeInfoBinding

class EmployeeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeInfoBinding

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