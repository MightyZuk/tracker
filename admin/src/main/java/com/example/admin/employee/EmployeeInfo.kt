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
import com.example.admin.Url
import com.example.admin.databinding.ActivityEmployeeInfoBinding
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import org.json.JSONArray

class EmployeeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeInfoBinding
    private lateinit var list: ArrayList<ClientModel>
    companion object{
        var locations = ""
    }

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
        binding.employeePassword.text = "password: ${intent.getIntExtra("password",0)}"
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
        val request = object :StringRequest(Method.POST,Url.getClientData,
            {
                val array = JSONArray(it)
                if (array.getString(0) == "success"){
                    Toast.makeText(this,"list is empty",Toast.LENGTH_SHORT).show()
                }else {
                    for (i in 0 until array.length()) {
                        val jsonObject = array.getJSONObject(i)
                        val id = jsonObject.getInt("employee_id")
                        val employeeName = jsonObject.getString("employee_name")
                        val name = jsonObject.getString("client_name")
                        val number = jsonObject.getInt("number")
                        val image = jsonObject.getString("image")
                        val location = jsonObject.getString("location")
                        val purpose = jsonObject.getString("purpose")
                        val amount = jsonObject.getInt("amount")
                        val dateTime = jsonObject.getString("date_time")

                        val end = destination(location)
                        val start = initial(location)
                        val distance = calculateDistance(location)

                        val client = ClientModel(id, employeeName, name, purpose, amount, start, end, location, distance, image, number,dateTime)

                        list.add(client)
                    }
                    val employeeAdapter =
                        ClientListAdapter(this, list, intent.getStringExtra("name").toString())
                    binding.visits.text = "Number of Visits: ${list.size}"
                    binding.clientList.adapter = employeeAdapter
                }
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

    private fun destination(loc: String): String{
        val re = loc.removeRange(0,1)
        val e = re.removeRange(re.length-2,re.length)
        val de = e.split(", ")
        val el = de[de.size-1].substring(0,de[de.size-1].indexOf(",")).toDouble()
        val eo = de[de.size-1].substring(de[de.size-1].indexOf(",").plus(1),de[de.size-1].length).toDouble()
        locations = "${el},${eo}"
        return locations
    }

    private fun initial(loc: String): String{
        val re = loc.removeRange(0,1)
        val e = re.removeRange(re.length-1,re.length)
        val de = e.split(", ")
        val el = de[0].substring(0,de[0].indexOf(",")).toDouble()
        val eo = de[0].substring(de[0].indexOf(",").plus(1),de[0].length).toDouble()
        locations = "${el},${eo}"
        return locations
    }

    private fun calculateDistance(loc: String): Float {
        var sum = 0F
        val re = loc.removeRange(0, 1)
        val e = re.removeRange(re.length - 1, re.length)
        val de = e.split(", ")

        var sla = de[0].substring(0, de[0].indexOf(",")).toDouble()
        var slo = de[0].substring(de[0].indexOf(",").plus(1), de[0].length).toDouble()

        for (i in 1 until de.size){
            val ela = de[i].substring(0, de[i].indexOf(",")).toDouble()
            val elo = de[i].substring(de[i].indexOf(",").plus(1), de[i].length).toDouble()

            val d = SphericalUtil.computeDistanceBetween(LatLng(sla,slo), LatLng(ela,elo))
            val distance = String.format("%.3f",d/1000).toFloat()

            sum += distance

            sla = ela
            slo = elo
        }
        return sum
    }

}