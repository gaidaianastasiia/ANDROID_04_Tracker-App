package com.example.trackerapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.trackerapp.domain.tracker.tracker_data.InsertUserLocationInteractor
import com.example.trackerapp.presentation.base.CoroutineIntentService
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.trackerapp.R
import com.example.trackerapp.utils.PermissionsManager
import javax.inject.Inject

private const val NOTIFICATION_CHANNEL_ID = "Location Service Id"
private const val START_FOREGROUND_ID = 1

class LocationService : CoroutineIntentService("LocationService") {
    @Inject
    lateinit var insertUserLocation: InsertUserLocationInteractor
    @Inject
    lateinit var permissionsManager: PermissionsManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        val channelId =
            createNotificationChannel(NOTIFICATION_CHANNEL_ID,
                getString(R.string.location_service_notification_channel_name))

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Title")
            .setContentText("Text")
            .setTicker("ticker_text")
            .build()

        startForeground(START_FOREGROUND_ID, notification)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val latitude = locationResult.lastLocation.latitude
                val longitude = locationResult.lastLocation.longitude

                launch(Dispatchers.IO) {
                    insertUserLocation(latitude, longitude)
                        .doOnError { error -> onStorageError(error) }
                }
            }
        }
    }


    override fun onHandleIntent(intent: Intent?) {
        val looper = Looper.getMainLooper()

        if(permissionsManager.isPermissionsGranted(this)) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
        }
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