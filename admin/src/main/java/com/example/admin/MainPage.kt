package com.example.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.admin.databinding.ActivityMainPageBinding

class MainPage : AppCompatActivity(), View.OnClickListener {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isSmsPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>admin</font>")
        val binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addEmployee.setOnClickListener(this)
        binding.employeeDetails.setOnClickListener(this)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permission ->
            isLocationPermissionGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
            isCameraPermissionGranted = permission[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            isSmsPermissionGranted = permission[Manifest.permission.READ_SMS] ?: isSmsPermissionGranted
        }

        runtimePermission()

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addEmployee -> {
                startActivity(Intent(this,AddEmployee::class.java))
            }
            R.id.employeeDetails -> {
                startActivity(Intent(this,EmployeeDetails::class.java))
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

}