package com.example.admin.employee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.app.NavUtils
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.R
import com.example.admin.databinding.ActivityEmployeeDetailsBinding
import org.json.JSONArray

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

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
        super.onBackPressed()
    }
    private fun getDataFromServer(){
//        val url = "http://192.168.1.49/Employee/getData.php" //intern
        val url = "http://192.168.1.7/Employee/getData.php" //home
        val request = StringRequest(
            Request.Method.GET,url,
            {
                val array = JSONArray(it)
                if (array.getString(0) == "success"){
                    Toast.makeText(this,"list is empty",Toast.LENGTH_SHORT).show()
                }else {
                    for (i in 0 until array.length()) {
                        val jsonObject = array.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val password = jsonObject.getInt("password")
                        val name = jsonObject.getString("name")
                        val image = jsonObject.getString("image")
                        val number = jsonObject.getInt("number")

                        val employeeData = EmployeeModel(id, password, name, image, number)
                        Log.d("request: ", employeeData.toString())


                        dataList.add(employeeData)
                    }
                    val listAdapter = EmployeeListAdapter(this, dataList)
                    binding.listOfEmployees.adapter = listAdapter
                }
            },
            { Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()})
        Volley.newRequestQueue(this).add(request)
    }
}