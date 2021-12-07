package com.example.tracker

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.widget.Toast
import com.example.tracker.databinding.ActivityFormBinding

class Form : AppCompatActivity() {

    private val id = 123
    private lateinit var binding: ActivityFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Form</font>")
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            Toast.makeText(this,"Client Details has been Regirstered",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Employee::class.java))
        }

        binding.takePicture.setOnClickListener {
            val openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(openCamera,id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == id){
            val photo = data!!.extras!!.get("data") as Bitmap
            binding.clientsImage.setImageBitmap(photo)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}