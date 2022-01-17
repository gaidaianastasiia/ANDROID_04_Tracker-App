package com.example.trackerapp.presentation.fragment.tracker

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
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
                    .setAction(R.string.ok, {
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

        viewModel.showCoveredDistance.observe(viewLifecycleOwner) {
            binding.distanceTextView.text = it.toString()
        }
    }

    private fun onFetchUserLocationsList(userLocationsList: List<UserLocation>) {
        showUserDistanceMapPolyline(userLocationsList)
        viewModel.getCoveredDistanceInMeters()

        if (userLocationsList.size > 0) {
            val lastUserLocation = userLocationsList.last()
            binding.speedTextView.text = lastUserLocation.speed.toString()
            moveMapCamera(lastUserLocation.location)
        }
    }

    private fun onStartTrackLocation() {
        viewModel.requestList()
        requireActivity().startForegroundService(
            Intent(requireContext(),
            LocationService::class.java)
        )
        removeDefaultUserLocationMapMarker()
        binding.trackerButton.setText(R.string.track_screen_stop_tracker_button_text)
    }

    private fun onStopTrackLocation() {
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
        removeUserDistanceMapPolyline()
        showCurrentUserLocation()
        binding.trackerButton.setText(R.string.track_screen_start_tracker_button_text)
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
}