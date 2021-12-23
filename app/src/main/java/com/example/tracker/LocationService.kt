package com.example.tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationService: Service() {

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.locations.let {
                for(i in it){
                    val latitude = i.latitude
                    val longitude = i.longitude
                    Log.d("location","$latitude , $longitude")
                    Toast.makeText(this@LocationService,"$latitude,$longitude",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startLocationService(){
        val channelId = "location_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent = PendingIntent
            .getActivities(applicationContext,0, arrayOf(resultIntent),PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat
            .Builder(applicationContext,channelId)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Location Service..")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running..")
        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setAutoCancel(false)
        builder.setContentIntent(pendingIntent)

        if (notificationManager.getNotificationChannel(channelId) == null){
            val notificationChannel =
                NotificationChannel(channelId,"Location Service..",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This channel is used by location Service.."
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val locationRequest = LocationRequest().apply {
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            this.interval = 1000 * 4
            this.interval = 1000 * 3
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())
        startForeground(175,builder.build())
    }

    private fun stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null){
            val action = intent.action
            if (action != null){
                if (action == "startLocationService"){
                    startLocationService()
                }else if (action == "stopLocationService"){
                    stopLocationService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}