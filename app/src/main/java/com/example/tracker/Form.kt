package com.example.tracker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tracker.databinding.ActivityFormBinding
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

class Form : AppCompatActivity() {

    private val id = 123
    private lateinit var binding: ActivityFormBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var list: Location? = null

    @SuppressLint("VisibleForTests", "MissingPermission", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Form</font>")
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE)
        sharedPreferences2 = getSharedPreferences("enw", MODE_PRIVATE)
        editor = sharedPreferences2.edit()

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val task = fusedLocationProviderClient.lastLocation
        task.addOnCompleteListener {
            list = it.result
        }

        binding.clientName.setText(intent.getStringExtra("name"))
        binding.clientPurpose.setText(intent.getStringExtra("purpose"))

        val start = Url.list[0]
        val sla = start.substring(0,start.indexOf(","))
        val slo = start.substring(start.indexOf(",").plus(1),start.length)
        val end = Url.list[Url.list.size-1]
        val ela = end.substring(0,end.indexOf(","))
        val elo = end.substring(end.indexOf(",").plus(1),end.length)


        binding.submit.setOnClickListener {
            val employeeId = sharedPreferences.getString("id",null)?.toInt()!!
            val employeeName = sharedPreferences.getString("name",null)
            val clientName = binding.clientName.text.toString()
            val purpose = binding.clientPurpose.text.toString()
            val amount = binding.amount.text.toString()
            val image = sharedPreferences2.getString("client_image",null)
            val initialLocation = "$sla,$slo"
            val finalLocation = "$ela,$elo"
            val number = binding.phone.text.toString()
            val sysTime = System.currentTimeMillis()
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a")
            val dateTime = formatter.format(sysTime)

            when{
                clientName.isEmpty() -> {
                    binding.clientName.requestFocus()
                    binding.clientName.error = "Required"
                }
                purpose.isEmpty() -> {
                    binding.clientPurpose.requestFocus()
                    binding.clientPurpose.error = "Required"
                }
                number.isEmpty() -> {
                    binding.phone.requestFocus()
                    binding.phone.error = "Required"
                }
                amount.isEmpty() -> {
                    binding.amount.requestFocus()
                    binding.amount.error = "Required"
                }
                number.length < 10 || number.length > 10 -> {
                    binding.phone.requestFocus()
                    binding.phone.error = "Please enter valid number"
                }
                else -> {
//                    Toast.makeText(this,dateTime,Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this,"${sharedPreferences2.getString("start",null)}",Toast.LENGTH_SHORT).show()
                    LocationStatus(this).stopLocationService()
                    for (i in Url.list){
                        Log.d("second : ",i)
                    }
                    val jsonArray = JSONArray(Url.list)
                    Log.d("array",jsonArray.toString())
                    putLocation(jsonArray,clientName)
//                    Toast.makeText(this,"stopped at: ${list?.latitude},${list?.longitude}",Toast.LENGTH_SHORT).show()
                    putClientDataToServer(employeeId,employeeName,clientName,purpose,amount,image,initialLocation,finalLocation,number,dateTime)
                }
            }
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

    private fun putLocation(jsonArray: JSONArray,clientName: String?){

        val request = object: StringRequest(Method.POST,Url.putLocation,
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
                map["json"] = jsonArray.toString()
                map["client_name"] = clientName.toString()
                return map
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun putClientDataToServer(employeeId: Int,employeeName: String?,clientName: String?,purpose: String?,
                                      amount: String?,image: String?,initialLocation: String?,finalLocation: String?,
                                      number: String?,dateTime: String?){

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
                    map["date_time"] = dateTime!!
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cancel,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cancel -> {
                LocationStatus(this).stopLocationService()
                startActivity(Intent(this,Employee::class.java))
                Toast.makeText(this,"Deal cancelled",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}