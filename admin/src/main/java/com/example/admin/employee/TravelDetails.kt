package com.example.admin.employee

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class TravelDetails : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var binding: ActivityTravelDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var list: ArrayList<List<Address>>
    private lateinit var locationPoints: MutableList<LatLng>

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = Html.fromHtml("<font color='#FFFFFF'>Travel Details</font>")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        binding = ActivityTravelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = ArrayList()
        locationPoints = ArrayList()

        sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        locationRequest = com.google.android.gms.location.LocationRequest().apply {
            this.interval = 1000 * 3
            this.fastestInterval = 1000 * 5
            this.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Glide.with(this).asBitmap()
            .load(intent.getStringExtra("image"))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.clientImage)

        binding.employeeName.text = intent.getStringExtra("emp_name")
        binding.clientName.text = intent.getStringExtra("client_name")
        binding.purpose.text = intent.getStringExtra("purpose")
//        binding.initialLocation.text = "start: ${intent.getIntExtra("initial", 0)}"
//        binding.destinationLocation.text = "end: ${intent.getIntExtra("final", 0)}"
        val lat = sharedPreferences.getFloat("latitude",0.0F).toString()
        val lon = sharedPreferences.getFloat("longitude",0.0F).toString()
        val geocoder = Geocoder(this)
        list.add(geocoder.getFromLocation(lat.toDouble(),lon.toDouble(),1))
        list.add(geocoder.getFromLocation(21.124, 78.75,1))

        val start = Location("A")
        start.latitude = lat.toDouble()
        start.longitude = lon.toDouble()

        val end = Location("B")
        end.latitude = 21.125621112626707
        end.longitude = 79.13657738521589

        locationPoints.add(LatLng(start.latitude,start.longitude))
        locationPoints.add(LatLng(21.126066459212886, 79.13548521060918))
        locationPoints.add(LatLng(21.125938234897237, 79.13538395722327))
        locationPoints.add(LatLng(21.12553080394413, 79.13598114956201))
        locationPoints.add(LatLng(21.12582204405324, 79.13623289964985))
        locationPoints.add(LatLng(end.latitude,end.longitude))


        binding.initialLocation.text = "start: ${locationPoints[0].latitude}, ${locationPoints[0].longitude}"
        binding.destinationLocation.text = "end: ${locationPoints[locationPoints.size-1].latitude},${locationPoints[locationPoints.size-1].longitude}"

        val distanceInMeters = start.distanceTo(end)
        val float = String.format("%.0f",distanceInMeters).toFloat()
        val km = float/1000
        binding.travelledDistance.text = "Travelled distance: $km km"
        updateGps()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }


    @SuppressLint("SetTextI18n")
    override fun onMapReady(map: GoogleMap) {
        val lat = sharedPreferences.getFloat("latitude",0.0F)
        val lon = sharedPreferences.getFloat("longitude",0.0F)

        map.addMarker(MarkerOptions()
            .title(list[0][0].getAddressLine(0))
            .position(locationPoints[0])
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        map.addPolyline(PolylineOptions()
            .color(R.color.teal_200)
            .clickable(true)
            .addAll(locationPoints))

        map.addMarker(MarkerOptions()
            .title(list[list.size-1][0].getAddressLine(0))
            .position(locationPoints[locationPoints.size-1])
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(),lon.toDouble()),16F))

    }


    @SuppressLint("SetTextI18n")
    private fun updateGps(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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
            dialog.setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            dialog.show()
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            editor.putFloat("latitude",it.latitude.toFloat())
            editor.putFloat("longitude",it.longitude.toFloat())
            editor.apply()
            Log.d("location",it.toString())
        }

    }

//    private fun drawableToBitmap(): BitmapDescriptor{
//        val drawable = ContextCompat.getDrawable(this,R.drawable.start)
//        drawable?.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
//        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,drawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        drawable.draw(canvas)
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }


}