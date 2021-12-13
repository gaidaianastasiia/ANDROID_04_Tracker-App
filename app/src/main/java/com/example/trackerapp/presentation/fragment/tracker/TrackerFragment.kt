package com.example.trackerapp.presentation.fragment.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trackerapp.databinding.FragmentTrackerBinding
import com.example.trackerapp.presentation.base.BaseFragment
import kotlin.reflect.KClass

class TrackerFragment :
    BaseFragment<
            TrackerViewModel,
            TrackerViewModel.Factory,
            FragmentTrackerBinding
            >() {
    override val viewModelClass: KClass<TrackerViewModel> = TrackerViewModel::class

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?,
    ): FragmentTrackerBinding = FragmentTrackerBinding.inflate(inflater, parent, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}