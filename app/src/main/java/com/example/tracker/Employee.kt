package com.example.tracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import com.example.tracker.databinding.ActivityEmployeeBinding
import com.google.android.material.button.MaterialButton

class Employee : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee</font>")
        setContentView(binding.root)

        val employeeAdapter = ClientListAdapter(this)
        binding.clientList.adapter = employeeAdapter

        binding.goForVisit.setOnClickListener(this)
        binding.refreshClient.setOnRefreshListener {
            binding.refreshClient.isRefreshing = false
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.goForVisit -> {
                popUp()
            }
        }
    }

    @SuppressLint("ResourceType")
    fun popUp(){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pop_up)
        dialog.create()
        dialog.show()
        dialog.findViewById<MaterialButton>(R.id.submit).setOnClickListener{
            dialog.dismiss()
            startActivity(Intent(this, Form::class.java))
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}