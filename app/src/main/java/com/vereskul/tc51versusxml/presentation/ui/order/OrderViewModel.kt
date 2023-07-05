package com.vereskul.tc51versusxml.presentation.ui.order

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.AppDb
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.repository.OrdersRepositoryImpl
import com.vereskul.tc51versusxml.data.util.checkBarcode
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import com.vereskul.tc51versusxml.domain.usecases.orders_case.BeginWorkUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.CloseOrderUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.EndWorkUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.GetOrderByIdUseCase
import com.vereskul.tc51versusxml.domain.usecases.orders_case.SendBarcodeTo1c
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrdersRepositoryImpl.getInstance(
        AppDb.getInstance(application),
        ApiFactory.apiService
    )

    private val getOrderByIdUseCase = GetOrderByIdUseCase(repository)
    private val beginWorkUseCase = BeginWorkUseCase(repository)
    private val endWorkUseCase = EndWorkUseCase(repository)
    private val sendBarcodeTo1cUseCase = SendBarcodeTo1c(repository)
    private val closeOrderUseCase = CloseOrderUseCase(repository)
    private val _order = MutableLiveData<SupplierOrderModel>()
    val order: LiveData<SupplierOrderModel>
        get()=_order
    fun setCurrentOrder(order: SupplierOrderModel) {
        _order.value = order
    }

    private val _orderFormState = MutableLiveData(OrderFormState())
    val orderFormState: LiveData<OrderFormState> = _orderFormState


    fun beginOrder(){
        viewModelScope.launch {
            order.value?.let {
                beginWorkUseCase(it)
                val orderModel = getOrderByIdUseCase(it.orderId)
                _order.value = orderModel
            }
        }

    }

    fun endOrder() = viewModelScope.launch {
        Log.d("OrderViewModel", "old order ${order.value}")
            order.value?.let {
                Log.d("OrderViewModel", it.orderId)
                endWorkUseCase(it)
                Log.d("OrderViewModel", "get order ${getOrderByIdUseCase(it.orderId)}")
                getOrderByIdUseCase(it.orderId).let {resultOrder ->
                    _order.value = resultOrder
                    Log.d("OrderViewModel", "$resultOrder")
                }
            }
        }

    fun getCurrentPageByBarcode(barcode: String): Int? = order.value?.goods?.indexOfFirst{ goods  ->
        goods.barcode == barcode
    }

    fun changeQtyFact(itemIndex: Int, value: Double){
        order.value?.let {
            val newValue = it.copy()
            newValue.goods[itemIndex].qtyFact += value
            if (newValue.goods[itemIndex].qtyFact >= 0){
                _order.value = newValue
            }
        }
    }

    fun checkGoodsBarcode(barcode: String): Boolean{
        return checkBarcode(barcode)
    }

    private fun isAllBarcodeCorrect(): Boolean{
        return _order.value?.let {
            it.goods.any {goodsModel->
                if (goodsModel.barcode==null){
                    true
                }else{
                    checkGoodsBarcode(goodsModel.barcode!!)
                }
            }
        }?:false
    }

    fun sendBarcodeTo1c(barcode: String) {
        viewModelScope.launch {
            val saveResult = sendBarcodeTo1cUseCase(barcode)
            //TODO: create event on result
        }
    }

    private fun isAllQtyCorrect(): Boolean{
        return _order.value?.let {
            !it.goods.any { row -> row.qty != row.qtyFact
            }
        }?:false
    }

    fun dataChanged(){
        if (!isAllQtyCorrect()) {
            _orderFormState.value = OrderFormState(qtyFactError = R.string.error_in_qty_quetion)
        }else if (!isAllBarcodeCorrect()){
            _orderFormState.value = OrderFormState(barcodeError = R.string.error_barcode_question)
        } else {
            _orderFormState.value = OrderFormState(isDataValid = true)
        }

    }


    fun closeOrder() =viewModelScope.launch{
        _order.value?.let {
            closeOrderUseCase(it)
        }
    }
}