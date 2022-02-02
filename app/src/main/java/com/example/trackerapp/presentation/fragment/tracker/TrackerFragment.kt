package com.example.trackerapp.presentation.fragment.tracker

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackerapp.R
import com.example.trackerapp.databinding.FragmentTrackerBinding
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.presentation.base.BaseFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.JointType.ROUND
import com.google.android.material.snackbar.Snackbar
import kotlin.reflect.KClass
import android.widget.Toast

import android.graphics.Bitmap

import android.os.Environment

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.content.ContextWrapper
import java.util.*

import android.graphics.BitmapFactory
import android.os.SystemClock
import android.widget.ImageView
import java.io.FileInputStream
import java.io.FileNotFoundException


private val TAG = TrackerFragment::class.java.simpleName
private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class TrackerFragment :
    BaseFragment<
            TrackerViewModel,
            TrackerViewModel.Factory,
            FragmentTrackerBinding
            >() {
    override val viewModelClass: KClass<TrackerViewModel> = TrackerViewModel::class
    lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userDistancePolyline: Polyline? = null
    private var defaultUserLocationMarker: Marker? = null
    private var offset: Long? = null
    private var imageName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trackerButton.isEnabled = false
        checkPermissions()
        setClickListeners()
        setObserve()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentTrackerBinding = FragmentTrackerBinding.inflate(inflater, parent, false)

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.trackerButton.isEnabled = true
                startMapInitialization()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {

                Snackbar.make(
                    binding.trackerFragment,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.permission_positive_button_text, {
                        requestLocationPermission()
                    })
                    .show()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        activityResultLauncher.launch(
            arrayOf(ACCESS_FINE_LOCATION)
        )
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val isGranted = permissions.entries.all { it.value }
            binding.trackerButton.isEnabled = isGranted
            if (isGranted) startMapInitialization()
        }

    private fun setClickListeners() {
        binding.trackerButton.setOnClickListener {
            viewModel.onTrackerButtonClick()
        }
    }

    private fun setObserve() {
        viewModel.userLocationsList.observe(viewLifecycleOwner) {
            onFetchUserLocationsList(it)
        }

        viewModel.startTrackUserLocation.observe(viewLifecycleOwner) { startTrackUserLocation ->
            if (startTrackUserLocation) {
                onStartTrackLocation()
            } else {
                onStopTrackLocation()
            }
        }

        viewModel.showCurrentSpeed.observe(viewLifecycleOwner) {
            binding.speedTextView.text = it.toString()
        }

        viewModel.showCoveredDistance.observe(viewLifecycleOwner) {
            binding.distanceTextView.text = it.toString()
        }

        viewModel.showAverageSpeed.observe(viewLifecycleOwner) {
            binding.averageSpeedTextView.text = it.toString()
        }
    }

    private fun onFetchUserLocationsList(userLocationsList: List<UserLocation>) {
        if (userLocationsList.isNotEmpty()) {
            showUserDistanceMapPolyline(userLocationsList)
            viewModel.getCoveredDistanceInMeters()
            viewModel.getUserAverageSpeed()
            moveMapCamera(userLocationsList.last().location)
        }
    }

    private fun onStartTrackLocation() {
        viewModel.requestList()
        requireActivity().startForegroundService(
            Intent(requireContext(),
                LocationService::class.java)
        )
        removeDefaultUserLocationMapMarker()
        startTimeCounter()
        binding.trackerButton.setText(R.string.track_screen_stop_tracker_button_text)
    }

    private fun onStopTrackLocation() {
        takeMapSnapShot()
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))

        stopTimeCounter()
        removeUserDistanceMapPolyline()
        showCurrentUserLocation()
        binding.trackerButton.setText(R.string.track_screen_start_tracker_button_text)
    }
    
    private fun startTimeCounter() {
        binding.timeChronometer.base = SystemClock.elapsedRealtime()
        binding.timeChronometer.start()
    }
    
    private fun stopTimeCounter() {
        binding.timeChronometer.stop()
        binding.timeChronometer.base = SystemClock.elapsedRealtime()
    }

    private fun startMapInitialization() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { onMapReady(it) }
    }

    private fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        showCurrentUserLocation()
    }

    private fun showCurrentUserLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                val currentUserLocation = LatLng(it.latitude, it.longitude)
                moveMapCamera(currentUserLocation)
                showDefaultUserLocationMapMarker(currentUserLocation)
            }
    }

    private fun moveMapCamera(userLocation: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18F))
    }

    private fun showDefaultUserLocationMapMarker(userLocation: LatLng) {
        if (defaultUserLocationMarker == null) {
            defaultUserLocationMarker = googleMap.addMarker(MarkerOptions()
                .position(userLocation)
                .title("Marker in User Location"))
        }
    }

    private fun removeDefaultUserLocationMapMarker() {
        if (defaultUserLocationMarker != null) {
            defaultUserLocationMarker?.remove()
            defaultUserLocationMarker = null
        }
    }

    private fun showUserDistanceMapPolyline(userLocations: List<UserLocation>) {
        if (userDistancePolyline != null) userDistancePolyline?.remove()

        userDistancePolyline = googleMap.addPolyline(PolylineOptions().apply {
            userLocations.forEach { add(it.location) }
            color(Color.BLUE)
            width(9F)
            jointType(ROUND)
        })
    }

    private fun removeUserDistanceMapPolyline() {
        if (userDistancePolyline != null) {
            userDistancePolyline?.remove()
            userDistancePolyline = null
        }
    }

    private fun takeMapSnapShot() {
        val callback: SnapshotReadyCallback = object : SnapshotReadyCallback {
            var bitmap: Bitmap? = null
            override fun onSnapshotReady(snapshot: Bitmap?) {
                bitmap = snapshot
                try {
                    bitmap?.let { saveMapImage(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        googleMap.snapshot(callback)
    }

    private fun saveMapImage(image: Bitmap) {
        val cw = ContextWrapper(requireContext())
        val directory = cw.getDir("profile", Context.MODE_APPEND)
        if (!directory.exists()) {
            directory.mkdir()
        }

        imageName = "map${Date().time}.png"
        val mypath = File(directory, imageName)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", e.message, e)
        }
    }

    private fun getMapImage() {
        try {
            val cw = ContextWrapper(requireContext())
            val path1 = cw.getDir("profile", Context.MODE_PRIVATE)
            val f = File(path1, imageName)
            val bitmap = BitmapFactory.decodeStream(FileInputStream(f))

            //todo
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}