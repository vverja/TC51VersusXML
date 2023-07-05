package com.vereskul.tc51versusxml.presentation.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.orders_case.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupplierOrdersViewModel(private val application: Application):AndroidViewModel(application) {
    private val repository = OrdersRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService
    )

    private val getAllOrders = GetAllOrdersUseCase(repository)
    private val getInnerOrdersByStatus = GetInnerOrdersByStatus(repository)
    private val getAdventOrdersByStatus = GetAdventOrdersByStatus(repository)

    private val _onlyInnerOrders = MutableStateFlow(false)
    val onlyInnerOrders = _onlyInnerOrders.asStateFlow()

    private val _orders = MutableLiveData<List<SupplierOrderModel>>()
    val orders: LiveData<List<SupplierOrderModel>>
        get() = _orders

    private val _countNewOrders = MutableLiveData(Pair(0,0))
    val countNewOrders: LiveData<Pair<Int, Int>> = _countNewOrders
    init {
        refreshFromRepository()
        dataChangesObserver()
    }

    private fun dataChangesObserver() {
        viewModelScope.launch {
            val changesObserver = repository.getDataChangesObserver()
            changesObserver.collect { dataChanged ->
                if (dataChanged) {
                    getAllOrders()
                    changesObserver.value = false
                }
            }
        }
    }


    fun getAllOrders(){
        viewModelScope.launch {
            onlyInnerOrders.value?.let {
                val orders = getAllOrders(it)
                _orders.value = orders
            }
        }
    }

    fun countNewOrders() {
        viewModelScope.launch {
            val income = getAdventOrdersByStatus(OrderStatus.NEW).size
            val outgo = getInnerOrdersByStatus(OrderStatus.NEW).size
            _countNewOrders.value = income to outgo
        }
    }

    fun isInnerOrders(inner: Boolean){
        _onlyInnerOrders.value = inner
    }

    fun getNewOrders(){
        viewModelScope.launch {
            val ordersByStatus = if(onlyInnerOrders.value) {
                getInnerOrdersByStatus(OrderStatus.NEW)
            }else{
                getAdventOrdersByStatus(OrderStatus.NEW)

            }
            _orders.value = ordersByStatus
        }
    }
    fun getInWorkOrders(){
        viewModelScope.launch {
            val ordersByStatus = if(onlyInnerOrders.value) {
                getInnerOrdersByStatus(OrderStatus.IN_WORK)
            }else{
                getAdventOrdersByStatus(OrderStatus.IN_WORK)

            }
            _orders.value = ordersByStatus
        }
    }
    fun getInStockOrders(){
        viewModelScope.launch {
            val ordersByStatus = if(onlyInnerOrders.value) {
                getInnerOrdersByStatus(OrderStatus.IN_STOCK)
            }else{
                getAdventOrdersByStatus(OrderStatus.IN_STOCK)

            }
            _orders.value = ordersByStatus
        }
    }
    fun getErrorOrders(){
        viewModelScope.launch {
            val ordersByStatus = if(onlyInnerOrders.value) {
                getInnerOrdersByStatus(OrderStatus.CANCELED)
            }else{
                getAdventOrdersByStatus(OrderStatus.CANCELED)

            }
            _orders.value = ordersByStatus
        }
    }
    fun refreshFromRepository() {
        viewModelScope.launch (Dispatchers.IO) {
            val uploadOrdersCount = repository.uploadOrders()
            val downloadOrdersCount = repository.downloadOrders()

            showNotification(downloadOrdersCount, uploadOrdersCount)
        }
        getAllOrders()
        countNewOrders()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(downloadOrdersCount: Int, uploadOrdersCount: Int) {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(application, Notification.EXTRA_CHANNEL_ID)
        }else{
            Notification.Builder(application)
        }
        builder.setContentTitle(application.getString(R.string.notify_title))
            .setContentText(
                application.getString(
                    R.string.notify_text,
                    downloadOrdersCount,
                    uploadOrdersCount
                )
            )
            .setSmallIcon(R.drawable.begin_work)
            .setPriority(Notification.PRIORITY_HIGH)
        val notificationManager = NotificationManagerCompat.from(application)
        notificationManager.notify(1, builder.build())
    }

    class Factory(val app: Application):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SupplierOrdersViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SupplierOrdersViewModel(app) as T
            }else{
                throw IllegalArgumentException("Unable to construct viewmodel")
            }
        }
    }
}