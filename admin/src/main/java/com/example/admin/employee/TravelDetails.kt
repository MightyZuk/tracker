package com.example.admin.employee

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.admin.R
import com.example.admin.databinding.ActivityTravelDetailsBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.internal.IGoogleMapDelegate
import com.google.android.gms.maps.model.*

class TravelDetails : AppCompatActivity(),OnMapReadyCallback{

    private lateinit var binding: ActivityTravelDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPoints: ArrayList<LatLng>
    private var polyline : Polyline? = null


    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Travel Details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityTravelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPoints = ArrayList()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)



        Glide.with(this).asBitmap()
            .load(intent.getStringExtra("image"))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.clientImage)

        binding.employeeName.text = intent.getStringExtra("emp_name")
        binding.clientName.text = intent.getStringExtra("client_name")
        binding.purpose.text = intent.getStringExtra("purpose")



//        binding.initialLocation.text = "start: ${locationPoints[0].latitude}, ${locationPoints[0].longitude}"
//        binding.destinationLocation.text = "end: ${locationPoints[locationPoints.size-1].latitude},${locationPoints[locationPoints.size-1].longitude}"

//        val distanceInMeters = start.distanceTo(end)
//        val float = String.format("%.0f",distanceInMeters).toFloat()
//        val km = float/1000
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        map.isMyLocationEnabled = true

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

        binding.travelledDistance.setOnClickListener {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            map.addPolyline(PolylineOptions().color(android.R.color.holo_orange_dark).add(locationPoints[locationPoints.size-1]))
            Toast.makeText(this,"stopped at : ${locationPoints[locationPoints.size-1].latitude},${locationPoints[locationPoints.size-1].longitude}",Toast.LENGTH_SHORT).show()
        }

        binding.initialLocation.setOnClickListener {
            map.addPolyline(PolylineOptions().add(locationPoints[0]).color(android.R.color.holo_orange_dark))
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                Looper.getMainLooper())
            Toast.makeText(this,"started",Toast.LENGTH_SHORT).show()
        }

    }


}