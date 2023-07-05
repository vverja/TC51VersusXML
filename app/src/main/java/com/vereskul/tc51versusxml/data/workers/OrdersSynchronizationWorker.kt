package com.vereskul.tc51versusxml.data.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.vereskul.tc51versusxml.R
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
                val uploadOrdersCount = ordersRepository.uploadOrders()
                val downloadOrdersCount = ordersRepository.downloadOrders()
                if (uploadOrdersCount>0 || downloadOrdersCount>0){
                    showNotification(uploadOrdersCount, downloadOrdersCount)
                }
            }
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(uploadOrdersCount: Int, downloadOrdersCount: Int){
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, Notification.EXTRA_CHANNEL_ID)
        }else{
            Notification.Builder(applicationContext)
        }
        builder.setContentTitle(applicationContext.getString(R.string.notify_title))
            .setContentText(
                applicationContext.getString(
                    R.string.notify_text,
                    downloadOrdersCount,
                    uploadOrdersCount
                )
            )
            .setSmallIcon(R.drawable.begin_work)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(1, builder.build())
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