package com.vereskul.tc51versusxml.presentation.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.orders_case.*
import kotlinx.coroutines.Dispatchers
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
    private fun refreshFromRepository()=viewModelScope.launch(Dispatchers.IO) {
        Log.d("SupplierOrdersViewModel", "refresh from db")
        repository.downloadOrders()
        repository.uploadOrders()
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