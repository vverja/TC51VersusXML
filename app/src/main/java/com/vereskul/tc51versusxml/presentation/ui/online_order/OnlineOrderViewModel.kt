package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OnlineOrderRepositoryImpl
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.ItemModel
import com.vereskul.tc51versusxml.domain.models.SaveResult
import com.vereskul.tc51versusxml.domain.models.StockModel
import com.vereskul.tc51versusxml.domain.models.SupplierModel
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetCurrentUserUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetItemsUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetStocksUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.GetSuppliersUseCase
import com.vereskul.tc51versusxml.domain.usecases.online_order_usecase.SaveOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime

class OnlineOrderViewModel(application: Application):AndroidViewModel(application) {
    private val repository = OnlineOrderRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService
    )

    private val getItemsUseCase = GetItemsUseCase(repository)
    private val getStocksUseCase = GetStocksUseCase(repository)
    private val getSuppliersUseCase = GetSuppliersUseCase(repository)
    private val saveOrderUseCase = SaveOrderUseCase(repository)
    private val currentUserUseCase = GetCurrentUserUseCase(repository)

    private var _suppliersList = MutableLiveData<List<SupplierModel>?>()
    val suppliersList: LiveData<List<SupplierModel>?>
        get() = _suppliersList

    private var _stocksList = MutableLiveData<List<StockModel>?>()
    val stocksList: LiveData<List<StockModel>?>
        get() = _stocksList

    private var _itemsList = MutableLiveData<List<ItemModel>?>()
    val itemsList: LiveData<List<ItemModel>?>
        get() = _itemsList

    private var _currentOrder = MutableLiveData(
        SupplierOrderModel(orderId = "", date = LocalDateTime.now()))

    val currentOrder: LiveData<SupplierOrderModel>
        get() = _currentOrder

    init {
        setDefaultOrder()
    }
    private fun setDefaultOrder() {
        viewModelScope.launch {
            val currentUser = currentUserUseCase()
            selectedStock = StockModel(currentUser?.stockCode ?: "", currentUser?.stockName ?: "")
            _currentOrder.value = SupplierOrderModel(
                    orderId = "",
                    date = LocalDateTime.now(),
                    stockCode = selectedStock?.code,
                    stock = selectedStock?.name
            )
        }
    }



    private var _currentGoodsList = MutableLiveData(
        listOf(GoodsModel())
    )
    val currentGoodsList: LiveData<List<GoodsModel>>
        get() = _currentGoodsList

    private var _totalSum = MutableLiveData(0.00)
    val totalSum: LiveData<Double> get() = _totalSum

    private val _formState = MutableStateFlow(OnlineOrderFormState())
    val formState = _formState.asStateFlow()

    private val _saveResult = MutableStateFlow(SaveResult())
    val saveResult = _saveResult.asStateFlow()

    var selectedSupplier: SupplierModel? = null
    var selectedStock: StockModel? = null

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
        _currentOrder.value?.amount = sum
    }

    fun addEmptyGoodsItem(){
        val newList = _currentGoodsList.value?.toMutableList().also {
            it?.add(GoodsModel())
        }
        _currentGoodsList.value =  newList?.toList()
        orderDataChanged()
    }

    private fun isSupplierValid(supplier: String?): Boolean {
        return supplier?.isNotBlank()?:false && supplier?.isNotEmpty()?:false
    }

    fun orderDataChanged()
    {
        val order = currentOrder.value
        val goodsList = currentGoodsList.value
        when {
            !isSupplierValid(order?.supplier) -> {
                _formState.value  = OnlineOrderFormState(
                    supplierError = R.string.supplier_name_error
                )
            }
            !isValidStock(order?.stock) -> {
                _formState.value = OnlineOrderFormState(
                    stockError = R.string.stock_name_error
                )
            }
            !isValidDate(order?.date) -> {
                _formState.value =  OnlineOrderFormState(
                    dateError = R.string.date_must_be_today
                )
            }
            !isGoodsListValid(goodsList) -> {
                OnlineOrderFormState(
                    goodsListError = getErrorFromList(order?.goods)
                )
            }
            else -> {
                _formState.value = OnlineOrderFormState(
                    isDataValid = true
                )
            }
        }

            
    }

    private fun getErrorFromList(goodsList: List<GoodsModel>?): Int {
        var rowId = 0
        if (!goodsList.isNullOrEmpty()) {
            for (goods in goodsList) {
                rowId++
                if(goods.name?.isBlank() != false
                    && goods.qty?.let { it < 0 } != false
                    && goods.price?.let { it < 0 } != false
                ){
                    return rowId
                }
            }
        }
        return 0
    }

    private fun isGoodsListValid(goodsList: List<GoodsModel>?): Boolean {
        if(goodsList?.size == 0)
            return false
        return goodsList?.count { goods ->
            goods.name?.isBlank() ?: true
                    && goods.qty?.let { it < 0 } ?: true
                    && goods.price?.let { it < 0 } ?: true
        } == 0
    }

    private fun isValidDate(orderDate: LocalDateTime?): Boolean {
        val today = LocalDate.now()
        return orderDate?.dayOfMonth == today.dayOfMonth
                && orderDate.month == today.month
                && orderDate.year == today.year
    }

    private fun isValidStock(stock: String?): Boolean {
        return stock?.isNotEmpty()?:false && stock?.isNotBlank()?:false
    }

    fun saveOrder(){
        viewModelScope.launch {
            val order = _currentOrder.value
            order?.apply {
                supplierCode = selectedSupplier?.code
                stockCode = selectedStock?.code
                goods = _currentGoodsList.value?: listOf()
                if (_formState.value.isDataValid){
                   _saveResult.value = saveOrderUseCase(this)
                }
            }
        }
    }

    companion object{
        const val TAG = "OnlineOrderViewModel"
    }

}