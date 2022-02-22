package com.example.trackerapp.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.Channel
import java.lang.ref.WeakReference

class PermissionsManager {

    private var weekActivity: WeakReference<AppCompatActivity>? = null
    private var permissionChannel: Channel<List<String>> = Channel()
    private var activityChannel: Channel<AppCompatActivity> = Channel()
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null


    private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
        activityLifecycleChanged(source, event)
    }

    fun attachToLifecycle(activity: AppCompatActivity) {
        weekActivity?.get()?.lifecycle?.removeObserver(lifecycleEventObserver)
        activity.lifecycle.addObserver(lifecycleEventObserver)
        initializeActivityResultLauncher(activity)
    }

    suspend fun requestPermissions(vararg requestedPermissions: String): Boolean {
        return if (isPermissionsGranted(awaitActivity(), *requestedPermissions)) {
            true
        } else {
            activityResultLauncher?.launch(requestedPermissions.toList()
                .toTypedArray())
            try {
                val grantedPermissions = permissionChannel.receive()
                val isGranted = grantedPermissions == requestedPermissions.toList()
                isGranted
            } finally {
                permissionChannel = Channel()
            }
        }
    }

    fun isPermissionsGranted(context: Context, vararg permissions: String): Boolean {
        return permissions.all { permission ->
            val result = ContextCompat.checkSelfPermission(context, permission)
            result == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializeActivityResultLauncher(activity: AppCompatActivity) {
        activityResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissions ->
                val grantedPermissions = permissions
                    .filter { (_, grantResult) -> grantResult == true }
                    .keys
                    .toList()

                permissionChannel.trySend(grantedPermissions)

            }
    }

    private suspend fun awaitActivity(): AppCompatActivity {
        return weekActivity?.get() ?: activityChannel.receive()
    }

    private fun activityLifecycleChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val activity = source as AppCompatActivity

        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                weekActivity = WeakReference(activity)
                activityChannel.trySend(activity)
            }
            Lifecycle.Event.ON_PAUSE -> weekActivity = null
            Lifecycle.Event.ON_DESTROY -> activity.lifecycle.removeObserver(
                lifecycleEventObserver)
            else -> Unit
        }
    }
}