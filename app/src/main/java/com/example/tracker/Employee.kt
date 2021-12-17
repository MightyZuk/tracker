package com.example.tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
import org.json.JSONObject


class Employee : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var dialog: Dialog
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isSmsPermissionGranted = false
    private lateinit var locationManager: LocationManager
    private lateinit var dataList: ArrayList<EmployeeModel>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Employee</font>")
        setContentView(binding.root)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        dataList = ArrayList()
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
        Glide.with(this).asBitmap().load(sharedPreferences.getString("image",null))
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
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.goForVisit -> {
                popUp()
            }
        }
    }

    @SuppressLint("ResourceType")
    fun popUp(){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pop_up)
        dialog.create()
        dialog.show()
        dialog.findViewById<MaterialButton>(R.id.submit).setOnClickListener{
            dialog.dismiss()
            startActivity(Intent(this,Form::class.java))
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
//        val url = "http://192.168.1.7/Employee/getData.php"//home
        val url = "http://192.168.1.49/Employee/getData.php" //intern
        val request = StringRequest(Request.Method.GET,url,
            {
                val array = JSONArray(it)

                for (i in 0 until array.length()){
                    val jsonObject = array.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val password = jsonObject.getInt("password")
//                    val employeeName = jsonObject.getString("password")
                    val name = jsonObject.getString("name")
                    val number = jsonObject.getInt("number")
                    val image = jsonObject.getString("image")
//                    val initial = jsonObject.getInt("initial_location")
//                    val final = jsonObject.getInt("final_location")
//                    val purpose = jsonObject.getString("purpose")
//                    val amount = jsonObject.getInt("amount")

                    val client = EmployeeModel(id,password,name, image, number)

                    dataList.add(client)
                }
                val employeeAdapter = ClientListAdapter(this,dataList)
                binding.numberOfVisits.text = "Number of Visits: ${dataList.size}"
                binding.clientList.adapter = employeeAdapter
            },
            {
                Toast.makeText(this,"Message: ${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("text",it.message.toString())
            })

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