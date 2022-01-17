package com.example.trackerapp.presentation.fragment.tracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.WorkerThread
import dagger.android.DaggerService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlin.coroutines.CoroutineContext

abstract class CoroutineIntentService(private val mName: String) : DaggerService(), CoroutineScope {
    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + coroutineJob
    private var mRedelivery: Boolean = false
    private lateinit var handlerActor: SendChannel<ActorMessage>

    override fun onCreate() {
        super.onCreate()
        handlerActor = handlerActor(mName)
    }

    override fun onStartCommand(@Nullable intent: Intent?, flags: Int, startId: Int): Int {
        runBlocking {
            val msg = ActorMessage()
            msg.arg1 = startId
            msg.obj = intent
            intent?.let { handlerActor.send(msg) }
        }
        return if (mRedelivery) Service.START_REDELIVER_INTENT else Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        coroutineJob.cancel()
    }


    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @WorkerThread
    protected abstract fun onHandleIntent(@Nullable intent: Intent?)

    private fun CoroutineScope.handlerActor(mName: String) = actor<ActorMessage>(
        context = coroutineJob + CoroutineName(mName),
        capacity = Channel.UNLIMITED,
        start = CoroutineStart.LAZY
    ) {
        for (msg in channel) {
            onHandleIntent(msg.obj as Intent)
        }
    }
}

data class ActorMessage(var what: Int = 0, var arg1: Int = 0, var obj: Any? = null)