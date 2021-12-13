package com.example.trackerapp.presentation.fragment.tracker

import androidx.lifecycle.SavedStateHandle
import com.example.trackerapp.presentation.base.BaseViewModel
import com.example.trackerapp.presentation.base.BaseViewModelAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TrackerViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle
) : BaseViewModel(savedStateHandle) {
    @AssistedFactory
    interface Factory : BaseViewModelAssistedFactory<TrackerViewModel>
}