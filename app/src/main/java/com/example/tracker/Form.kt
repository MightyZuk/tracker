package com.example.tracker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tracker.databinding.ActivityFormBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.io.ObjectInput
import java.net.URL

class Form : AppCompatActivity() {

    private val id = 123
    private lateinit var binding: ActivityFormBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var list: ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Form</font>")
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE)
        sharedPreferences2 = getSharedPreferences("enw", MODE_PRIVATE)
        editor = sharedPreferences2.edit()


        list = ArrayList()

        binding.clientName.setText(intent.getStringExtra("name"))
        binding.clientPurpose.setText(intent.getStringExtra("purpose"))

        binding.submit.setOnClickListener {
            val employeeId = sharedPreferences.getString("id",null)?.toInt()!!
            val employeeName = sharedPreferences.getString("name",null)
            val clientName = binding.clientName.text.toString()
            val purpose = binding.clientPurpose.text.toString()
            val amount = binding.amount.text.toString()
            val image = sharedPreferences2.getString("client_image",null)
            val initialLocation = intent.getStringExtra("start")
            val finalLocation = intent.getStringExtra("end")
            val number = binding.phone.text.toString()

            putClientDataToServer(employeeId,employeeName,clientName,purpose,amount,image,initialLocation,finalLocation,number)
        }

        binding.takePicture.setOnClickListener {
            val openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(openCamera,id)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == id){
            val photo = data!!.extras!!.get("data") as Bitmap
            binding.clientsImage.setImageBitmap(photo)
            val stringImage = bitmapToString(photo)
            editor.putString("client_image",stringImage)
            editor.apply()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    private fun putClientDataToServer(employeeId: Int,employeeName: String?,clientName: String?,purpose: String?,
                                      amount: String?,image: String?,initialLocation: String?,finalLocation: String?,
                                      number: String?){

        val request = object: StringRequest(Method.POST,Url.putClientData,
            {
                if (it.equals("Data inserted successfully",true)){
                    startActivity(Intent(this, Employee::class.java))
                    Toast.makeText(this,"Data inserted successfully",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Failed to insert data",Toast.LENGTH_SHORT).show()
                    Log.d("request : ",it)
                }
            },
            {
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
            })
            {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    map["employee_id"] = employeeId.toString()
                    map["employee_name"] = employeeName.toString()
                    map["client_name"] = clientName!!
                    map["image"] = image!!
                    map["purpose"] = purpose!!
                    map["amount"] = amount!!
                    map["initial_location"] = initialLocation!!
                    map["final_location"] = finalLocation!!
                    map["number"] = number!!
                    return map
                }
            }
        Volley.newRequestQueue(this).add(request)
    }

    private fun bitmapToString(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesOfImage = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
    }

}