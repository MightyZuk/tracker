package com.example.admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.admin.databinding.ActivityAddEmployeeBinding
import com.google.android.material.button.MaterialButton
import java.security.Permissions
import java.util.jar.Manifest
import kotlin.random.Random
import kotlin.random.nextInt

class AddEmployee : AppCompatActivity() {

    private lateinit var binding: ActivityAddEmployeeBinding
    private val imageId : Int = 12
    private lateinit var image : Bitmap


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Register Employee</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.takePicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(camera, imageId)
            }else{
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog)
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
                dialog.findViewById<TextView>(R.id.title).text = "Please turn on Camera"
                dialog.findViewById<MaterialButton>(R.id.ok).setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
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