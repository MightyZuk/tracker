package com.example.tracker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

class LocationStatus(val context: Context) {

    fun isLocationRunning(): Boolean{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService::class.java.name.equals(service.service.className)) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    fun startLocationService(){
        if (!isLocationRunning()){
            val intent = Intent(context.applicationContext,LocationService::class.java)
            intent.action = "startLocationService"
            context.startService(intent)
            Toast.makeText(context,"Location service started",Toast.LENGTH_SHORT).show()
        }
    }

    fun stopLocationService(){
        if (isLocationRunning()){
            val intent = Intent(context.applicationContext,LocationService::class.java)
            intent.action = "stopLocationService"
            context.startService(intent)
            Toast.makeText(context,"Location Service Stopped",Toast.LENGTH_SHORT).show()
        }
    }
}