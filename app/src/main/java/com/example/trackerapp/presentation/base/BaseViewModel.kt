package com.example.trackerapp.presentation.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerapp.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    protected val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _storageErrorResponse = SingleLiveEvent<Unit>()
    val storageErrorResponse: LiveData<Unit>
        get() = _storageErrorResponse

    fun onStorageError(error: Throwable) {
        viewModelScope.launch(Dispatchers.Main) {
            _storageErrorResponse.call()
            Log.e(this::class.simpleName, error.toString())
        }
    }
}