package com.example.tracker

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tracker.databinding.ActivityGetInBinding
import org.json.JSONObject

class GetIn : AppCompatActivity() {

    private lateinit var binding: ActivityGetInBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGetInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val isLoggedIn = sharedPreferences.getBoolean("isLogin",false)

        if (isLoggedIn){
            startActivity(Intent(this,Employee::class.java))
            finish()
        }

        binding.getIn.setOnClickListener{
            val id = binding.uniqueId.text
            val password = binding.password.text
            when{
                id.isEmpty() -> {
                    binding.uniqueId.requestFocus()
                    binding.uniqueId.error = "Required"
                }
                password.isEmpty() -> {
                    binding.password.requestFocus()
                    binding.password.error = "Required"
                }
                id.length < 6 -> {
                    binding.uniqueId.requestFocus()
                    binding.uniqueId.error = "Please enter valid id"
                }
                password.length < 6 -> {
                    binding.password.requestFocus()
                    binding.password.error = "Please enter valid password"
                }
                else -> {
                    checkLoginInfo(id.toString(),password.toString())
                }
            }
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    private fun checkLoginInfo(id: String,password: String){

        val request = object :StringRequest(Method.POST,Url.login,
            {
                val jsonObject = JSONObject(it)
                val success = jsonObject.getString("success")
                val message = jsonObject.getString("message")

                if (success.equals("1")){
                    val identity = jsonObject.getString("id")
                    val name = jsonObject.getString("name")
                    val image = jsonObject.getString("image")

                    editor.putBoolean("isLogin",true)
                    editor.putString("id",identity)
                    editor.putString("employee_image",image)
                    editor.putString("name",name)
                    editor.apply()
                    Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Employee::class.java))
                    finish()
                }else{
                    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                }

            },
            {
                Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show()})
        {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String,String>()
                map["id"] = id
                map["password"] = password
                return map
            }
        }
        Log.d("tag: ",request.toString())
        Volley.newRequestQueue(this).add(request)
    }
}