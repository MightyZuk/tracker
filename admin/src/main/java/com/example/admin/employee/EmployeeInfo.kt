package com.example.admin.employee

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.example.admin.client.ClientListAdapter
import com.example.admin.client.ClientModel
import com.example.admin.R
import com.example.admin.databinding.ActivityEmployeeInfoBinding
import org.json.JSONArray

class EmployeeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeInfoBinding
    private lateinit var list: ArrayList<ClientModel>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee Details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityEmployeeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = ArrayList()
        getClientDataFromServer()

        val clientAdapter = ClientListAdapter(this,list,intent.getStringExtra("name")!!)
        binding.clientList.setHasFixedSize(true)
        binding.clientList.adapter = clientAdapter

        binding.employeeId.text = "id: ${intent.getIntExtra("id",0)}"
        binding.employeeName.text = "${intent.getStringExtra("name")}"
        Glide.with(this).asBitmap().load(intent.getStringExtra("image"))
            .fitCenter()
            .centerInside()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.employeeImage)

        binding.refreshClient.setOnRefreshListener {
            list.clear()
            getClientDataFromServer()
            binding.refreshClient.isRefreshing = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("SetTextI18n")
    private fun getClientDataFromServer(){
        val url = "http://192.168.1.7/Employee/getData.php"//home
//        val url = "http://192.168.1.49/Employee/getClientData.php" //intern
        val request = object :StringRequest(Method.POST,url,
            {
                val array = JSONArray(it)
                for (i in 0 until array.length()){
                    val jsonObject = array.getJSONObject(i)
                    val id = jsonObject.getInt("employee_id")
                    val employeeName = jsonObject.getString("employee_name")
                    val name = jsonObject.getString("client_name")
                    val number = jsonObject.getInt("number")
                    val image = jsonObject.getString("image")
                    val initial = jsonObject.getInt("initial_location")
                    val final = jsonObject.getInt("final_location")
                    val purpose = jsonObject.getString("purpose")
                    val amount = jsonObject.getInt("amount")

                    val client = ClientModel(id,employeeName,name,purpose,amount,initial,final,image,number)

                    list.add(client)
                }
                val employeeAdapter = ClientListAdapter(this,list,intent.getStringExtra("name").toString())
                binding.visits.text = "Number of Visits: ${list.size}"
                binding.clientList.adapter = employeeAdapter
            },
            {
                Toast.makeText(this,"Message: ${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("text",it.message.toString())
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String,String>()
                map["employee_id"] = intent.getIntExtra("id",0).toString()
                map["employee_name"] = intent.getStringExtra("name").toString()
                return map
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

}