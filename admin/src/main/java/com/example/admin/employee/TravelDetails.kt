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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import com.google.android.libraries.places.api.Places
import com.google.maps.android.SphericalUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class TravelDetails : AppCompatActivity(),OnMapReadyCallback{

    private lateinit var binding: ActivityTravelDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPoints: ArrayList<LatLng>

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

        Places.initialize(this,"AIzaSyCtFiLyzGtC47l1DGCSAJBMePdTb32G668")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        Glide.with(this).asBitmap()
            .load(intent.getStringExtra("image"))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.clientImage)

        binding.employeeName.text = intent.getStringExtra("emp_name")
        binding.clientName.text = intent.getStringExtra("client_name")
        binding.purpose.text = intent.getStringExtra("purpose")
        binding.initialLocation.text = "start: ${intent.getStringExtra("initial")}"
        binding.destinationLocation.text = "end: ${intent.getStringExtra("final")}"
        binding.travelledDistance.text = "Travelled distance: ${intent.getFloatExtra("distance",0f)}km"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onMapReady(map: GoogleMap) {
        val start = intent.getStringExtra("initial")
        val sla = start?.substring(0,start.indexOf(","))
        val slo = start?.substring(start.indexOf(",").plus(1),start.length)
        val end = intent.getStringExtra("final")
        val ela = end?.substring(0,end.indexOf(","))
        val elo = end?.substring(end.indexOf(",").plus(1),end.length)

        val re = intent.getStringExtra("locations")!!.removeRange(0,1)
        val e = re.removeRange(re.length-2,re.length)
        val de = e.split(", ")
        Log.d("de",de.toString())

        val polylineOptions = PolylineOptions().color(R.color.purple_200)
        for (l in de){
            val slat = l.substring(0,l.indexOf(","))
            val slon = l.substring(l.indexOf(",").plus(1),l.length)

            val point = LatLng(slat.toDouble(),slon.toDouble())
            polylineOptions.add(point)
            Log.d("points ",point.toString())
        }
        map.addPolyline(polylineOptions)


        val geocoder = Geocoder(this,Locale.getDefault())
        val startGeo = geocoder.getFromLocation(sla!!.toDouble(), slo!!.toDouble(),10)
        val endGeo = geocoder.getFromLocation(ela!!.toDouble(), elo!!.toDouble(),10)

        map.addMarker(MarkerOptions().title("start at : ${startGeo[0].getAddressLine(0)}")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .position(LatLng(sla.toDouble(), slo.toDouble())))

        map.addMarker(MarkerOptions().title("end at: ${endGeo[0].getAddressLine(0)}")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .position(LatLng(ela.toDouble(), elo.toDouble())))


        map.setLatLngBoundsForCameraTarget(
            LatLngBounds(LatLng(sla.toDouble().minus(0.1), slo.toDouble()), LatLng(ela.toDouble().plus(0.1),elo.toDouble())))

        val center = LatLngBounds.builder()
            .include(LatLng(sla.toDouble(),slo.toDouble())).include(LatLng(ela.toDouble(),elo.toDouble()))
            .build().center

        map.moveCamera(CameraUpdateFactory
            .newLatLngZoom(center,16F))


    }
}