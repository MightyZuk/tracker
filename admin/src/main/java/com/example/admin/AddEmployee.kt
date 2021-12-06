package com.example.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.app.NavUtils
import com.example.admin.databinding.ActivityAddEmployeeBinding
import kotlin.random.Random
import kotlin.random.nextInt

class AddEmployee : AppCompatActivity() {

    private lateinit var binding: ActivityAddEmployeeBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Register Employee</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.getStringExtra("id")
        val pass = intent.getStringExtra("password")
        val name = intent.getStringExtra("name")

        if (id != null && pass != null){
            binding.id.visibility = View.VISIBLE
            binding.password.visibility = View.VISIBLE
            binding.names.visibility = View.VISIBLE
            binding.names.text = "$name your"
            binding.id.text = "id: $id"
            binding.password.text = "password: $pass"
        }


        binding.register.setOnClickListener {
            when{
                binding.name.text!!.isEmpty() -> {
                    binding.name.requestFocus()
                    binding.name.error = "Required"
                }
                binding.number.text!!.isEmpty() -> {
                    binding.number.requestFocus()
                    binding.number.error = "Required"
                }
                binding.number.text!!.length < 10 -> {
                    binding.number.requestFocus()
                    binding.number.error = "Please enter a valid number"
                }
                binding.number.text!!.length > 10 -> {
                    binding.number.requestFocus()
                    binding.number.error = "Please enter a valid number"
                }
                else -> {
                    Toast.makeText(this,"Otp has been sent to your registered number",Toast.LENGTH_SHORT).show()
                    Intent(this,OtpVerification::class.java).also {
                        it.putExtra("name",binding.name.text.toString())
                        startActivity(it)
                    }
                }

            }
        }
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }

}