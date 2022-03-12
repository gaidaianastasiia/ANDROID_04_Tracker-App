package com.example.trackerapp.presentation.base

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(
    protected val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    fun onStorageError(error: Throwable) {
        Log.e(this::class.simpleName, error.toString())

    }
}