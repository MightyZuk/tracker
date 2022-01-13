package com.example.tracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.*
import com.example.tracker.databinding.ActivityEmployeeBinding
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tracker.R
import com.example.tracker.adapter.ClientListAdapter
import com.example.tracker.model.ClientModel
import com.example.tracker.service.LocationStatus
import com.example.tracker.usable.Url
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.util.stream.IntStream.range


class Employee : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var dialog: Dialog
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false
    private lateinit var locationManager: LocationManager
    private lateinit var dataList: ArrayList<ClientModel>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var location: Location ?= null
    private lateinit var employeeAdapter: ClientListAdapter

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee</font>")
        setContentView(binding.root)


        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: isLocationPermissionGranted
                isCameraPermissionGranted =
                    permission[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            }

        runtimePermission()

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        dataList = ArrayList()

        if (isLocationPermissionGranted && isCameraPermissionGranted) {
            fetchClientDataFromServer()
            Handler(Looper.getMainLooper()).postDelayed({ checkGPS() }, 500)


        }
        employeeAdapter = ClientListAdapter(this, dataList)
        binding.clientList.adapter = employeeAdapter

        binding.goForVisit.setOnClickListener(this)

        binding.refreshClient.setOnRefreshListener {
            dataList.clear()
            fetchClientDataFromServer()
            employeeAdapter.notifyDataSetChanged()
            binding.refreshClient.isRefreshing = false
        }

        binding.employeeId.text = "id: ${sharedPreferences.getString("id",null)}"
        binding.employeePassword.text = "password: ${sharedPreferences.getString("password",null)}"
        binding.employeeName.text = sharedPreferences.getString("name",null)
        Glide.with(this).asBitmap().load(sharedPreferences.getString("employee_image",null))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.employeeImage)

        if(LocationStatus(this).isLocationRunning()){
            startActivity(Intent(this,Form::class.java))
        }

        binding.clientList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !binding.goForVisit.isShown) binding.goForVisit.show()
                else if (dy > 0 && binding.goForVisit.isShown) binding.goForVisit.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCREEN_STATE_ON) binding.goForVisit.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.goForVisit -> {
                editor.putString("start","${location?.latitude},${location?.longitude}")
                editor.apply()
//                Toast.makeText(this,"started at: ${location?.latitude},${location?.longitude}",Toast.LENGTH_SHORT).show()
                Url.list.clear()
                LocationStatus(this).startLocationService()
                popUp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ResourceType")
    fun popUp(){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pop_up)
        dialog.create()
        dialog.show()
        dialog.findViewById<MaterialButton>(R.id.submit).setOnClickListener{
            dialog.dismiss()
            Intent(this,Form::class.java).also { intent ->
                intent.putExtra("name",dialog.findViewById<EditText>(R.id.clientName).text.toString())
                intent.putExtra("purpose",dialog.findViewById<EditText>(R.id.purpose).text.toString())
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    private fun runtimePermission(){
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        isCameraPermissionGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val requests: MutableList<String> = ArrayList()

        if (!isLocationPermissionGranted){
            requests.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!isCameraPermissionGranted){
            requests.add(Manifest.permission.CAMERA)
        }
        if (requests.isNotEmpty()){
            permissionLauncher.launch(requests.toTypedArray())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkGPS(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog)
            dialog.create()
            dialog.show()
            dialog.setCancelable(false)
            dialog.findViewById<TextView>(R.id.title).text = "Please turn on GPS"
            dialog.findViewById<MaterialButton>(R.id.ok).setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent,12)
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchClientDataFromServer(){
        val request = @RequiresApi(Build.VERSION_CODES.N)
        object :StringRequest(Method.POST, Url.getClientData,
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
                        val purpose = jsonObject.getString("purpose")
                        val amount = jsonObject.getInt("amount")
                        val dateTime = jsonObject.getString("date_time")
                        val location = jsonObject.getString("location")

                        val res = destinationLocationData(location)
                        val distance = calculateDistance(location)
                        val client = ClientModel(id, employeeName, name, purpose, amount, distance, res, image, number, dateTime)

                        dataList.add(client)
                    }
                    val employeeAdapter = ClientListAdapter(this, dataList)
                    binding.numberOfVisits.text = "Number of Visits: ${dataList.size}"
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
                map["employee_id"] = sharedPreferences.getString("id",null).toString()
                map["employee_name"] = sharedPreferences.getString("name",null).toString()
                return map
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOut ->{
                editor.putBoolean("isLogin",false)
                editor.apply()
                startActivity(Intent(this,GetIn::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun destinationLocationData(loc: String): String{
        val re = loc.removeRange(0,1)
        val e = re.removeRange(re.length-1,re.length)
        val de = e.split(", ")
        val el = de[de.size-1].substring(0,de[de.size-1].indexOf(",")).toDouble()
        val eo = de[de.size-1].substring(de[de.size-1].indexOf(",").plus(1),de[de.size-1].length).toDouble()
        return "${el},${eo}"
    }

    private fun calculateDistance(loc: String): Float {
        val re = loc.removeRange(0, 1)
        val e = re.removeRange(re.length - 1, re.length)
        val de = e.split(", ")

        var sla = de[0].substring(0, de[0].indexOf(",")).toDouble()
        var slo = de[0].substring(de[0].indexOf(",").plus(1), de[0].length).toDouble()

        var sum = 0F
        for (i in 1 until de.size){
            val ela = de[i].substring(0, de[i].indexOf(",")).toDouble()
            val elo = de[i].substring(de[i].indexOf(",").plus(1), de[i].length).toDouble()

            val d = SphericalUtil.computeDistanceBetween(LatLng(sla,slo),LatLng(ela,elo))
            val distance = String.format("%.3f",d/1000).toFloat()

            sum += distance

            sla = ela
            slo = elo
        }
        return sum
    }
}
