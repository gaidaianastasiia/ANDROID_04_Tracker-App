package com.example.trackerapp.presentation.fragment.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trackerapp.R
import com.example.trackerapp.databinding.FragmentTrackerBinding
import com.example.trackerapp.presentation.base.BaseFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.reflect.KClass

class TrackerFragment :
    BaseFragment<
            TrackerViewModel,
            TrackerViewModel.Factory,
            FragmentTrackerBinding
            >() {
    override val viewModelClass: KClass<TrackerViewModel> = TrackerViewModel::class
    lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { onMapReady(it) }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentTrackerBinding = FragmentTrackerBinding.inflate(inflater, parent, false)

    private fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}