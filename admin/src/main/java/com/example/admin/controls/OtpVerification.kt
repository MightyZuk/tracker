package com.example.admin.controls

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.Url
import com.example.admin.databinding.ActivityOtpVerificationBinding
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class OtpVerification : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val id = Random.nextInt(100000,999999)
        val password = Random.nextInt(100000,999999)
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("number")
        val bitmapImage = intent.extras?.getParcelable<Bitmap>("image")
        val image = bitmapToString(bitmapImage!!)

        binding.verify.setOnClickListener {
            Intent(this, EmployeeGeneratedDetails::class.java).also {
                it.putExtra("id",id)
                it.putExtra("password",password)
                it.putExtra("name",name.toString())
                it.putExtra("number",number.toString())
                it.putExtra("image",bitmapImage)
                startActivity(it)
            }
            setDataToServer(id,password,name,number,image)
        }
    }

    private fun setDataToServer(id: Int,password: Int,name: String?,number: String?,image: String) {
        val request = object: StringRequest(Method.POST,Url.putData,
            {
                if (it.equals("Data Inserted Successfully",true)){
                    Toast.makeText(this,"Data Inserted",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"User already exists",Toast.LENGTH_SHORT).show()
                    Log.d("res: ",it.toString())
                }
            },
            {
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String,String>()
                map["id"] = id.toString()
                map["password"] = password.toString()
                map["name"] = name!!
                map["number"] = number!!
                map["image"] = image
                return map
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun bitmapToString(image: Bitmap): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
        val bytesOfImage = byteArrayOutputStream.toByteArray()
        val encodedImageString: String = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
        return encodedImageString
    }
}