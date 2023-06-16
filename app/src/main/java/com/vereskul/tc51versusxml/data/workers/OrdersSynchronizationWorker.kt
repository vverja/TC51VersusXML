package com.vereskul.tc51versusxml.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl
import com.vereskul.tc51versusxml.domain.OrdersRepository

class OrdersSynchronizationWorker(
    appContext: Context,
    parameters: WorkerParameters,
    private var ordersRepository: OrdersRepository
):CoroutineWorker(appContext, parameters) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("OrdersSynchronizationWorker", "Worker is working...")
            if (ApiFactory.isLoggedIn){
                ordersRepository.downloadOrders()
                ordersRepository.uploadOrders()
            }
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

    class Factory: WorkerFactory(){
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {

            return OrdersSynchronizationWorker(
                appContext,
                workerParameters,
                ordersRepository = OrdersRepositoryImpl.getInstance(
                    AppDb.getInstance(appContext),
                    ApiFactory.apiService
                )
            )
        }

    }
}