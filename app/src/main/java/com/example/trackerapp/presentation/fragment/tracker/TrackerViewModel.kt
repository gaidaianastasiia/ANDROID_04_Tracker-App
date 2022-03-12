package com.example.trackerapp.presentation.fragment.tracker

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.trackerapp.domain.tracker.tracker_data.DeleteAllUserLocationsInteractor
import com.example.trackerapp.domain.tracker.tracker_data.GetAllUserLocationsInteractor
import com.example.trackerapp.domain.tracker.tracker_data.GetAverageSpeedInteractor
import com.example.trackerapp.domain.tracker.tracker_data.GetCoveredDistanceInteractor
import com.example.trackerapp.domain.walk.InsertWalkInteractor
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.presentation.base.BaseViewModel
import com.example.trackerapp.presentation.base.BaseViewModelAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TRACK_STATE_SAVED_STATE_HANDLE_KEY = "Track State SavedStateHandle key"

class TrackerViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getAllUserLocations: GetAllUserLocationsInteractor,
    private val deleteAllUserLocations: DeleteAllUserLocationsInteractor,
    private val getCoveredDistance: GetCoveredDistanceInteractor,
    private val getAverageSpeed: GetAverageSpeedInteractor,
    private val insertWalk: InsertWalkInteractor,
) : BaseViewModel(savedStateHandle) {
    
    @AssistedFactory
    interface Factory : BaseViewModelAssistedFactory<TrackerViewModel>

    private val _coveredDistance = MutableLiveData<Double>()
    val coveredDistance: LiveData<Double>
        get() = _coveredDistance

    private val _averageSpeed = MutableLiveData<Double>()
    val averageSpeed: LiveData<Double>
        get() = _averageSpeed

    private val _currentSpeed = MutableLiveData<Double>()
    val currentSpeed: LiveData<Double>
        get() = _currentSpeed

    private val _startTrackUserLocation = MutableLiveData<Boolean>()
    val startTrackUserLocation: LiveData<Boolean>
        get() = _startTrackUserLocation

    private val _userLocationsList = MutableLiveData<List<UserLocation>>()
    val userLocationsList: LiveData<List<UserLocation>>
        get() = _userLocationsList

    private val _showLoader = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private var isUserLocationTracking =
        savedStateHandle.get(TRACK_STATE_SAVED_STATE_HANDLE_KEY) ?: false

    private var userLocationsJob: Job? = null
    private var coveredDistanceJob: Job? = null
    private var averageSpeedJob: Job? = null

    fun onTrackerButtonClick() {
        if (isUserLocationTracking) {
            setStopTrackLocationState()
        } else {
            setStartTrackLocationState()
        }
    }

    fun saveWalkData(context: Context, walkMapImage: Bitmap?) {
        viewModelScope.launch {
            insertWalk(context, walkMapImage)
                .doOnSuccess {
                    stopTrackLocation()
                }
                .doOnError { error ->
                    onStorageError(error)
                }
        }
    }

    private fun setStartTrackLocationState() {
        isUserLocationTracking = true
        _startTrackUserLocation.value = true
        savedStateHandle.set(TRACK_STATE_SAVED_STATE_HANDLE_KEY, true)
        requestList()
    }

    private fun requestList() {
        userLocationsJob = viewModelScope.launch {
            getAllUserLocations()
                .collect {
                    _userLocationsList.value = it

                    if (it.isNotEmpty()) {
                        handleCoveredDistance()
                        handleUserAverageSpeed()
                        handleCurrentSpeed(it)
                    }
                }
        }
    }

    private fun handleCoveredDistance() {
        coveredDistanceJob = viewModelScope.launch {
            getCoveredDistance()
                .collect {
                    _coveredDistance.value = it
                }
        }
    }

    private fun handleUserAverageSpeed() {
        averageSpeedJob = viewModelScope.launch {
            getAverageSpeed().collect {
                _averageSpeed.value = it
            }
        }
    }

    private fun handleCurrentSpeed(userLocationsList: List<UserLocation>) {
        _currentSpeed.value = userLocationsList.last().speed
    }

    private fun setStopTrackLocationState() {
        _startTrackUserLocation.value = false
        _showLoader.value = true
        savedStateHandle.set(TRACK_STATE_SAVED_STATE_HANDLE_KEY, false)
    }

    private suspend fun stopTrackLocation() {
        _showLoader.value = false
        isUserLocationTracking = false
        _currentSpeed.value = 0.0
        userLocationsJob?.cancel()
        coveredDistanceJob?.cancel()
        averageSpeedJob?.cancel()
        deleteAllUserLocations()
    }
}