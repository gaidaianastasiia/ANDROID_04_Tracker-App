package com.example.trackerapp.presentation.fragment.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.trackerapp.domain.DeleteAllUserLocationsInteractor
import com.example.trackerapp.domain.GetAllUserLocationsInteractor
import com.example.trackerapp.domain.GetAverageSpeedInteractor
import com.example.trackerapp.domain.GetCoveredDistanceInteractor
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.presentation.base.BaseViewModel
import com.example.trackerapp.presentation.base.BaseViewModelAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrackerViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getAllUserLocations: GetAllUserLocationsInteractor,
    private val deleteAllUserLocations: DeleteAllUserLocationsInteractor,
    private val getCoveredDistance: GetCoveredDistanceInteractor,
    private val getAverageSpeed: GetAverageSpeedInteractor,
) : BaseViewModel(savedStateHandle) {
    @AssistedFactory
    interface Factory : BaseViewModelAssistedFactory<TrackerViewModel>

    private val _showCoveredDistance = MutableLiveData<Double>()
    val showCoveredDistance: LiveData<Double>
        get() = _showCoveredDistance

    private val _showAverageSpeed = MutableLiveData<Double>()
    val showAverageSpeed: LiveData<Double>
        get() = _showAverageSpeed

    private val _showCurrentSpeed = MutableLiveData<Double>()
    val showCurrentSpeed: LiveData<Double>
        get() = _showCurrentSpeed

    private val _startTrackUserLocation = MutableLiveData<Boolean>()
    val startTrackUserLocation: LiveData<Boolean>
        get() = _startTrackUserLocation

    private val _userLocationsList = MutableLiveData<List<UserLocation>>()
    val userLocationsList: LiveData<List<UserLocation>>
        get() = _userLocationsList

    private var isUserLocationTracking = false
    private var userLocationsJob: Job? = null
    private var coveredDistanceJob: Job? = null
    private var averageSpeedJob: Job? = null

    fun onTrackerButtonClick() {
        if (isUserLocationTracking) {
            stopTrackLocation()
        } else {
            startTrackLocation()
        }
    }

    private fun requestList() {
        userLocationsJob = viewModelScope.launch {
            getAllUserLocations()
                .collect {
                    _userLocationsList.value = it

                    if (it.isNotEmpty()) {
                        getCoveredDistanceInMeters()
                        getUserAverageSpeed()
                        getCurrentSpeed(it)
                    }
                }
        }
    }

    private fun getCoveredDistanceInMeters() {
        coveredDistanceJob = viewModelScope.launch {
            getCoveredDistance()
                .collect {
                    _showCoveredDistance.value = it
                }
        }
    }

    private fun getUserAverageSpeed() {
        averageSpeedJob = viewModelScope.launch {
            getAverageSpeed().collect {
                _showAverageSpeed.value = it
            }
        }
    }

    private fun startTrackLocation() {
        isUserLocationTracking = true
        _startTrackUserLocation.value = true
        requestList()
    }

    private fun stopTrackLocation() {
        isUserLocationTracking = false
        _startTrackUserLocation.value = false
        userLocationsJob?.cancel()
        coveredDistanceJob?.cancel()
        averageSpeedJob?.cancel()
        _showCurrentSpeed.value = 0.0

        viewModelScope.launch {
            deleteAllUserLocations()
        }
    }

    private fun getCurrentSpeed(userLocationsList: List<UserLocation>) {
        _showCurrentSpeed.value = userLocationsList.last().speed
    }
}