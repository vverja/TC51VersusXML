package com.vereskul.tc51versusxml.presentation.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl

import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.orders_case.*
import com.vereskul.tc51versusxml.data.network.ApiFactory
import kotlinx.coroutines.launch

class SupplierOrdersViewModel(application: Application):AndroidViewModel(application) {
    private val repository = OrdersRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService
    )

    private val getAllOrdersInStockUseCase = GetOrdersByStockIdUseCase(repository)
    private val getNewOrdersUseCase = GetNewOrdersUseCase(repository)
    private val getInWorkOrdersUseCase = GetInWorkOrdersUseCase(repository)
    private val getInStockOrdersUseCase = GetInStockOrdersUseCase(repository)
    private val getCanceledOrdersUseCase = GetCanceledOrdersUseCase(repository)

    init {
        refreshFromRepository()
    }

    private var _orders = MutableLiveData<List<SupplierOrderModel>>()
    val orders: LiveData<List<SupplierOrderModel>>
        get() = _orders

    fun getAllOrders(){
        getAllOrdersInStockUseCase().observeForever {
            _orders.value = it
        }
    }

    fun getNewOrders(){
        getNewOrdersUseCase().observeForever {
            _orders.value = it
        }
    }
    fun getInWorkOrders(){
        getInWorkOrdersUseCase().observeForever {
            _orders.value = it
        }
    }
    fun getInStockOrders(){
        getInStockOrdersUseCase().observeForever {
            _orders.value = it
        }
    }
    fun getErrorOrders(){
        getCanceledOrdersUseCase().observeForever {
            _orders.value = it
        }
    }
    private fun refreshFromRepository()=viewModelScope.launch {
        repository.refreshOrdersInDb()
        getAllOrders()
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