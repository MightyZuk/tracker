package com.example.tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tracker.databinding.ActivityEmployeeBinding
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import java.io.StringReader
import java.lang.reflect.Method
import com.android.volley.DefaultRetryPolicy
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject


class Employee : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var dialog: Dialog
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isSmsPermissionGranted = false
    private lateinit var locationManager: LocationManager
    private lateinit var dataList: ArrayList<ClientModel>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var locationPoints: MutableList<LatLng>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee</font>")
        setContentView(binding.root)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        dataList = ArrayList()
        locationPoints = ArrayList()
        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        fetchClientDataFromServer()
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permission ->
            isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
            isCameraPermissionGranted = permission[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            isSmsPermissionGranted = permission[Manifest.permission.READ_SMS] ?: isSmsPermissionGranted
        }


        runtimePermission()
        Handler(Looper.getMainLooper()).postDelayed({checkGPS()},500)

        val employeeAdapter = ClientListAdapter(this,dataList)
        binding.clientList.adapter = employeeAdapter

        binding.goForVisit.setOnClickListener(this)
        binding.refreshClient.setOnRefreshListener {
            dataList.clear()
            fetchClientDataFromServer()
            binding.refreshClient.isRefreshing = false
        }

        binding.employeeId.text = "id: ${sharedPreferences.getString("id",null)}"
        binding.employeeName.text = sharedPreferences.getString("name",null)
        Glide.with(this).asBitmap().load(sharedPreferences.getString("employee_image",null))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.employeeImage)


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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = com.google.android.gms.location.LocationRequest().apply {
            this.interval = 1000 * 5
            this.fastestInterval = 1000 * 3
            this.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.let {
                    for (i in it){
                        locationPoints.add(LatLng(i.latitude,i.longitude))
                        Toast.makeText(applicationContext,"${i.latitude},${i.longitude}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.goForVisit -> {
                Toast.makeText(this,"started at : ",Toast.LENGTH_SHORT).show()
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())
                popUp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ResourceType")
    fun popUp(){
        dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.pop_up)
        dialog.create()
        dialog.show()
        dialog.findViewById<MaterialButton>(R.id.submit).setOnClickListener{
            dialog.dismiss()
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            Toast.makeText(this,"stopped at : ${locationPoints[locationPoints.size-1].latitude},${locationPoints[locationPoints.size-1].longitude}",Toast.LENGTH_SHORT).show()
            Intent(this,Form::class.java).also { intent ->
                intent.putExtra("start","${locationPoints[0].latitude},${locationPoints[0].longitude}")
                intent.putExtra("end","${locationPoints[locationPoints.size-1].latitude},${locationPoints[locationPoints.size-1].longitude}")
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

        isSmsPermissionGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

        val requests: MutableList<String> = ArrayList()

        if (!isLocationPermissionGranted){
            requests.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!isCameraPermissionGranted){
            requests.add(Manifest.permission.CAMERA)
        }
        if (!isSmsPermissionGranted){
            requests.add(Manifest.permission.READ_SMS)
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
                        val initial = jsonObject.getString("initial_location")
                        val final = jsonObject.getString("final_location")
                        val purpose = jsonObject.getString("purpose")
                        val amount = jsonObject.getInt("amount")

                        val client = ClientModel(id, employeeName, name, purpose, amount, initial, final, image, number)

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


}