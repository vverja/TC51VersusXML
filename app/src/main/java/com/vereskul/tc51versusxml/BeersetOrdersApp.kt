package com.vereskul.tc51versusxml

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vereskul.tc51versusxml.data.workers.OrdersSynchronizationWorker
import java.util.concurrent.TimeUnit

class BeersetOrdersApp:Application() {
    override fun onCreate() {
        super.onCreate()
        setupWorkManger()
    }

    private fun setupWorkManger(){
        val workerConfiguration = Configuration
            .Builder()
            .setWorkerFactory(OrdersSynchronizationWorker.Factory())
            .build()
        WorkManager.initialize(this, workerConfiguration)

        val periodicWorkRequest = PeriodicWorkRequestBuilder<OrdersSynchronizationWorker>(
            10, TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            OrdersSynchronizationWorker::class.java.name,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}