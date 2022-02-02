package com.example.trackerapp.presentation.fragment.tracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.trackerapp.domain.InsertUserLocationInteractor
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject

class LocationService : CoroutineIntentService("LocationService") {
    @Inject
    lateinit var insertUserLocation: InsertUserLocationInteractor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        val channelId =
            createNotificationChannel("my_service", "My Background Service")

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Title")
            .setContentText("Text")
            .setTicker("ticker_text")
            .build()

        startForeground(1, notification)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val latitude = locationResult.lastLocation.latitude
                val longitude = locationResult.lastLocation.longitude

                launch(Dispatchers.IO) {
                    insertUserLocation(latitude, longitude)
                        .doOnSuccess {

                    }
                        .doOnError { error -> onStorageError(error) }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onHandleIntent(intent: Intent?) {
        val looper = Looper.getMainLooper()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    fun onStorageError(error: Throwable) {
        launch(Dispatchers.Main) {
            Log.e(this::class.simpleName, error.toString())
        }
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }
}