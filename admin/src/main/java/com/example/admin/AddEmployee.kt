package com.example.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NavUtils
import com.example.admin.databinding.ActivityAddEmployeeBinding
import kotlin.random.Random
import kotlin.random.nextInt

class AddEmployee : AppCompatActivity() {

    private lateinit var binding: ActivityAddEmployeeBinding
    private val imageId : Int = 12
    private lateinit var image : Bitmap

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Register Employee</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.takePicture.setOnClickListener {
            val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera,imageId)
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
                        it.putExtra("number",binding.number.text.toString())
                        it.putExtra("image",image)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == imageId){
            image = data?.extras!!.get("data") as Bitmap
            binding.employeeImage.setImageBitmap(image)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}