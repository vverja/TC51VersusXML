package com.vereskul.tc51versusxml.presentation.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.orders_case.BeginWorkUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.EndWorkUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.GetOrderByIdUseCase
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrdersRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService
    )

    private val getOrderByIdUseCase = GetOrderByIdUseCase(repository)
    private val beginWorkUseCase = BeginWorkUseCase(repository)
    private val endWorkUseCase = EndWorkUseCase(repository)

    private val _order = MutableLiveData<SupplierOrderModel>()
    val order: LiveData<SupplierOrderModel>
        get()=_order

    private val _orderBeginSuccess = MutableLiveData<Boolean>()
    val orderBeginSuccess: LiveData<Boolean>
        get()=_orderBeginSuccess

    private val _orderEndSuccess = MutableLiveData<Boolean>()
    val orderEndSuccess: LiveData<Boolean>
        get()=_orderEndSuccess


}