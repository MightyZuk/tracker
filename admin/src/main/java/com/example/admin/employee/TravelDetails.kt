package com.example.admin.employee

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.admin.R
import com.example.admin.databinding.ActivityTravelDetailsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.button.MaterialButton

class TravelDetails : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var binding: ActivityTravelDetailsBinding
    private lateinit var locationManager: LocationManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Travel Details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityTravelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Glide.with(this).asBitmap()
            .load(intent.getStringExtra("image"))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.clientImage)
        binding.employeeName.text = intent.getStringExtra("emp_name")
        binding.clientName.text = intent.getStringExtra("client_name")
        binding.purpose.text = intent.getStringExtra("purpose")
        binding.initialLocation.text = "start: ${intent.getIntExtra("initial",0)}"
        binding.destinationLocation.text = "end: ${intent.getIntExtra("final",0)}"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }


    @SuppressLint("SetTextI18n")
    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("Location permission required")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Ok"){_, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName,null)
                intent.data = uri
                startActivity(intent)
            }
            dialog.show()
        }

        map.isMyLocationEnabled = true
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        map.setOnMyLocationButtonClickListener {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
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
            false
            }
        }

}