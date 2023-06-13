package com.vereskul.tc51versusxml.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class OrdersSynchronizationWorker(
    appContext: Context,
    parameters: WorkerParameters
):CoroutineWorker(appContext, parameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}