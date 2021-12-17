package com.example.admin.controls

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.admin.R
import com.example.admin.databinding.ActivityMainPageBinding
import com.example.admin.employee.EmployeeDetails
import com.google.android.material.button.MaterialButton

class MainPage : AppCompatActivity(), View.OnClickListener {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isSmsPermissionGranted = false
    private lateinit var locationManager: LocationManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>admin</font>")
        val binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addEmployee.setOnClickListener(this)
        binding.employeeDetails.setOnClickListener(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permission ->
            isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
            isCameraPermissionGranted = permission[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            isSmsPermissionGranted = permission[Manifest.permission.READ_SMS] ?: isSmsPermissionGranted
        }

        runtimePermission()

        Handler(Looper.getMainLooper()).postDelayed({checkGPS()},500)

        if (!checkInternet()){
            Toast.makeText(this,"Please connect to Internet",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addEmployee -> {
                startActivity(Intent(this, AddEmployee::class.java))
            }
            R.id.employeeDetails -> {
                startActivity(Intent(this, EmployeeDetails::class.java))
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

    private fun checkInternet(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null

    }
}