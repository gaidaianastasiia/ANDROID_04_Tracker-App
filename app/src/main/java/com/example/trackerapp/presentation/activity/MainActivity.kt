package com.example.trackerapp.presentation.activity

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.trackerapp.R
import com.example.trackerapp.databinding.ActivityMainBinding
import com.example.trackerapp.presentation.base.BaseActivity
import com.example.trackerapp.presentation.fragment.tracker.TrackerFragment
import kotlin.reflect.KClass

class MainActivity : BaseActivity<MainViewModel, MainViewModel.Factory, ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onCreate()
        }

        viewModel.startEvent.observe(this) {
            startToDoFragment()
        }
    }

    override val viewModelClass: KClass<MainViewModel> = MainViewModel::class

    override fun createViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    private fun startToDoFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<TrackerFragment>(R.id.fragmentContainer)
        }
    }
}

