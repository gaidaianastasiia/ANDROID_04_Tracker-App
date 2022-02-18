package com.example.trackerapp.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.trackerapp.R
import com.example.trackerapp.databinding.ActivityMainBinding
import com.example.trackerapp.presentation.base.BaseActivity
import com.example.trackerapp.presentation.fragment.tracker.TrackerFragment
import com.example.trackerapp.presentation.fragment.walk_list.WalkListFragment
import com.example.trackerapp.utils.PermissionsManager
import javax.inject.Inject
import kotlin.reflect.KClass

class MainActivity : BaseActivity<MainViewModel, MainViewModel.Factory, ActivityMainBinding>() {

    @Inject
    lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsManager.attachToLifecycle(this)

        if (savedInstanceState == null) {
            viewModel.onCreate()
        }

        viewModel.startEvent.observe(this) {
            startTrackerFragment()
        }
    }

    override val viewModelClass: KClass<MainViewModel> = MainViewModel::class

    override fun createViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    private fun startTrackerFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<WalkListFragment>(R.id.fragmentContainer)
        }
    }
}

