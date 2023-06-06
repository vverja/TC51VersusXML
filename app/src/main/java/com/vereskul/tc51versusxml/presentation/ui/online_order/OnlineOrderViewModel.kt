package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vereskul.tc51versusxml.database.AppDb
import com.vereskul.tc51versusxml.domain.models.*
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetItemsUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetStocksUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetSuppliersUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.SaveOrderUseCase
import com.vereskul.tc51versusxml.network.ApiFactory
import com.vereskul.tc51versusxml.repository.OnlineOrderRepositoryImpl
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OnlineOrderViewModel(application: Application):AndroidViewModel(application) {
    private val repository = OnlineOrderRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService!!
    )

    private val getItemsUseCase = GetItemsUseCase(repository)
    private val getStocksUseCase = GetStocksUseCase(repository)
    private val getSuppliersUseCase = GetSuppliersUseCase(repository)
    private val saveOrderUseCase = SaveOrderUseCase(repository)

    private var _suppliersList = MutableLiveData<List<SupplierModel>?>()
    val suppliersList: LiveData<List<SupplierModel>?>
        get() = _suppliersList

    private var _stocksList = MutableLiveData<List<StockModel>?>()
    val stocksList: LiveData<List<StockModel>?>
        get() = _stocksList

    private var _itemsList = MutableLiveData<List<ItemModel>?>()
    val itemsList: LiveData<List<ItemModel>?>
        get() = _itemsList

    private var _currentOrder = MutableLiveData<SupplierOrderModel>(
        SupplierOrderModel(orderId = "")
    )
    val currentOrder: LiveData<SupplierOrderModel>
        get() = _currentOrder

    private var _currentGoodsList = MutableLiveData<List<GoodsModel>>(
        listOf()
    )
    val currentGoodsList: LiveData<List<GoodsModel>>
        get() = _currentGoodsList

    private var _totalSum = MutableLiveData(0.00)
    val totalSum: LiveData<Double> get() = _totalSum

    fun getItems(){
        viewModelScope.launch {
            try {
                _itemsList.value = getItemsUseCase()
            } catch (e: HttpException) {
                Log.e(TAG, "getItemsUseCase ${e.message}")
            }
        }
    }

    fun getItemByName(name: String):ItemModel?{
        _itemsList.value?.let {
            return it.first { item-> item.name == name }
        }
        return null
    }

    fun getSuppliers(){
        viewModelScope.launch {
            try {
                _suppliersList.value = getSuppliersUseCase()
            } catch (e: HttpException) {
                Log.e(TAG, "getSuppliersUseCase ${e.message}")
            }
        }
    }

    fun getStocks(){
        viewModelScope.launch {
            try {
                _stocksList.value = getStocksUseCase()
            } catch (e: HttpException) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun countTotalSum(){
        val sum = _currentGoodsList.value?.map { (it.qty ?: 0.00) * (it.price ?: 0.00) }?.sum()
        _totalSum.value = sum
    }

    fun addEmptyGoodsItem(){
        val newList = _currentGoodsList.value?.toMutableList().also {
            it?.add(GoodsModel())
        }
        _currentGoodsList.value =  newList?.toList()
    }

    companion object{
        const val TAG = "OnlineOrderViewModel"
    }

}