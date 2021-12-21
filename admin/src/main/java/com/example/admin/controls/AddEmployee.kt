package com.example.admin.controls

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.R
import com.example.admin.Url
import com.example.admin.databinding.ActivityAddEmployeeBinding
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

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
                    sendOtp(binding.number.text.toString())
                    checkDataFromServer(binding.number.text.toString(),binding.name.text.toString())
                }

            }
        }

    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }

    private fun checkDataFromServer(number: String,name: String){
        val request = object: StringRequest(Method.POST,Url.checkNumber,
            {
                if (!it.equals("user already exists",true)){
                    Intent(this, OtpVerification::class.java).also { intent ->
                        intent.putExtra("name",name)
                        intent.putExtra("number",number)
                        intent.putExtra("image",image)
                        startActivity(intent)
                    }
                    Toast.makeText(this,"Otp has been sent to your registered number",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"User already exists",Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String,String>()
                map["number"] = number
                return map
            }
        }
        Volley.newRequestQueue(this).add(request)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == imageId){
            image = data?.extras!!.get("data") as Bitmap
            binding.employeeImage.setImageBitmap(image)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendOtp(number: String){
        val otp = Random.nextInt(100000,999999)
        val intent = PendingIntent.getBroadcast(this,0,Intent("SMS_SENT"),0)
        SmsManager.getDefault().sendTextMessage(number,null,"Otp is $otp",intent,null)
    }

}