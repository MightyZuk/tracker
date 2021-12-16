package com.example.admin

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.ViewAnimator
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityEmployeeDetailsBinding
import org.json.JSONArray
import java.io.ByteArrayOutputStream

class EmployeeDetails : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDetailsBinding
    private lateinit var dataList: ArrayList<EmployeeModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee's</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityEmployeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataList = ArrayList()
        getDataFromServer()

        val listAdapter = EmployeeListAdapter(this,dataList)
        binding.listOfEmployees.adapter = listAdapter

        binding.refreshEmployees.setOnRefreshListener {
            dataList.clear()
            getDataFromServer()
            binding.refreshEmployees.isRefreshing = false
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun getDataFromServer(){
//        val url = "http://192.168.1.49/Employee/getData.php" //intern
        val url = "http://192.168.1.7/Employee/getData.php"
        val request = StringRequest(
            Request.Method.GET,url,
            {
                val array = JSONArray(it)

                for (i in 0 until array.length()){
                    val jsonObject = array.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val password = jsonObject.getInt("password")
                    val name = jsonObject.getString("name")
                    val image = jsonObject.getString("image")
                    val number = jsonObject.getInt("number")

                    val employeeData = EmployeeModel(id,password,name,image,number)
                    Log.d("request: ",employeeData.toString())


                    dataList.add(employeeData)
                }
                val listAdapter = EmployeeListAdapter(this,dataList)
                binding.listOfEmployees.adapter = listAdapter
            },
            { Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()})
        Volley.newRequestQueue(this).add(request)
    }
}