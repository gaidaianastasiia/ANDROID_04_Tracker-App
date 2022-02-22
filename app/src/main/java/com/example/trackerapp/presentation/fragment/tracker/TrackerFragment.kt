package com.example.trackerapp.presentation.fragment.tracker

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import java.lang.Exception
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.trackerapp.services.LocationService
import com.example.trackerapp.utils.PermissionsManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackerFragment :
    BaseFragment<
            TrackerViewModel,
            TrackerViewModel.Factory,
            FragmentTrackerBinding
            >() {

    override val viewModelClass: KClass<TrackerViewModel> = TrackerViewModel::class
    @Inject
    lateinit var permissionsManager: PermissionsManager
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userDistancePolyline: Polyline? = null
    private var userLocationMapMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setObserve()
        doWithLocationPermissions {
            binding.trackerButton.isEnabled = true
            startMapInitialization()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentTrackerBinding = FragmentTrackerBinding.inflate(inflater, parent, false)


    private fun setClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

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

    private fun doWithLocationPermissions(onPermissionResult: () -> Unit) {
        lifecycleScope.launch {
            when {
                permissionsManager.requestPermissions(ACCESS_FINE_LOCATION) -> {
                    onPermissionResult()
                }
                !shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                    Snackbar.make(
                        binding.trackerFragment,
                        R.string.permission_rationale,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.permission_action_button_text) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", requireContext().packageName, null)
                            })
                        }
                        .show()
                }
            }
        }
    }

    private fun onFetchUserLocationsList(userLocationsList: List<UserLocation>) {
        if (userLocationsList.isNotEmpty()) {
            showUserDistanceMapPolyline(userLocationsList)
            moveMapCamera(userLocationsList.last().location)
        }
    }

    private fun onStartTrackLocation() {
        requireActivity().startForegroundService(
            Intent(requireContext(),
                LocationService::class.java)
        )
        removeUserLocationMapMarker()
        startTimeCounter()
        binding.trackerButton.setText(R.string.track_screen_stop_tracker_button_text)
    }

    private fun onStopTrackLocation() {
        saveWalkData()
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
        stopTimeCounter()
        removeUserDistanceMapPolyline()
        showCurrentUserLocation()
        binding.trackerButton.setText(R.string.track_screen_start_tracker_button_text)
    }

    private fun saveWalkData() {
        val callback: SnapshotReadyCallback = object : SnapshotReadyCallback {
            var walkMapImage: Bitmap? = null
            override fun onSnapshotReady(snapshot: Bitmap?) {
                walkMapImage = snapshot
                try {
                    viewModel.saveWalkData(requireContext(), walkMapImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        googleMap.snapshot(callback)
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
        doWithLocationPermissions {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    val currentUserLocation = LatLng(it.latitude, it.longitude)
                    moveMapCamera(currentUserLocation)
                    showUserLocationMapMarker(currentUserLocation)
                }
        }
    }

    private fun moveMapCamera(userLocation: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15F))
    }

    private fun showUserLocationMapMarker(userLocation: LatLng) {
        if (userLocationMapMarker == null) {
            userLocationMapMarker = googleMap.addMarker(MarkerOptions()
                .position(userLocation)
                .title("Marker in User Location"))
        }
    }

    private fun removeUserLocationMapMarker() {
        if (userLocationMapMarker != null) {
            userLocationMapMarker?.remove()
            userLocationMapMarker = null
        }
    }

    private fun showUserDistanceMapPolyline(userLocations: List<UserLocation>) {
        if (userDistancePolyline != null) userDistancePolyline?.remove()

        userDistancePolyline = googleMap.addPolyline(PolylineOptions().apply {
            userLocations.forEach { add(it.location) }
            color(R.color.orange_200)
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
}