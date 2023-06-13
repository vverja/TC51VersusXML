package com.vereskul.tc51versusxml.presentation.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.InnerOrdersListItemBinding
import com.vereskul.tc51versusxml.databinding.OrdersListItemBinding
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import java.time.format.DateTimeFormatter

class OrdersListAdapter:
    androidx.recyclerview.widget.ListAdapter
         <SupplierOrderModel, OrdersListAdapter.OrderListViewHolder>(
                SupplierOrderDiffCallback()
            ){
    var onOrderClickListener:((SupplierOrderModel)->Unit)? = null

    class OrderListViewHolder(private val binding: ViewDataBinding):ViewHolder(binding.root){
        fun bind(
            supplierOrderModel: SupplierOrderModel,
            onOrderClickListener: ((SupplierOrderModel)->Unit)?){
            var orderNumber: TextView? = null
            var orderSupplier: TextView? = null
            var orderDate: TextView? = null
            var orderImg: ImageView? = null

            if (binding is InnerOrdersListItemBinding) {
                orderNumber = binding.textNumber
                orderSupplier = binding.textSupplier
                orderDate = binding.textDate
                orderImg = binding.imageOrderState
                orderSupplier.text = supplierOrderModel.stock
            }else if(binding is OrdersListItemBinding){
                orderNumber = binding.textNumber
                orderSupplier = binding.textSupplier
                orderDate = binding.textDate
                orderImg = binding.imageOrderState
                orderSupplier.text = supplierOrderModel.supplier
            }

            binding.root.setOnClickListener {
                onOrderClickListener?.invoke(supplierOrderModel)
            }
            with(supplierOrderModel){
                orderNumber?.text = number

                orderDate?.text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                var circleId = when(orderState){
                    OrderStatus.NEW -> R.drawable.green_circle
                    OrderStatus.IN_WORK -> R.drawable.yellow_circle
                    OrderStatus.CANCELED -> R.drawable.multiply
                    OrderStatus.IN_STOCK ->R.drawable.blue_circle
                    else -> throw RuntimeException("unknown order status")
                    }
                    orderImg?.background = ContextCompat.getDrawable(binding.root.context, circleId)
            }


        }
    }
    class SupplierOrderDiffCallback:DiffUtil.ItemCallback<SupplierOrderModel>(){
        override fun areItemsTheSame(
            oldItem: SupplierOrderModel,
            newItem: SupplierOrderModel
        ): Boolean {
            return oldItem.number==newItem.number
        }

        override fun areContentsTheSame(
            oldItem: SupplierOrderModel,
            newItem: SupplierOrderModel
        ): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        if (viewType == IS_ORDER) {
            return OrderListViewHolder(
                OrdersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }else{
            return OrderListViewHolder(
                InnerOrdersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order,onOrderClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        val order = getItem(position)
        order.supplier?.let {
            if(it.isBlank() or it.isEmpty()){
                return IS_INNER
            }
        }
        return IS_ORDER
    }


    companion object{
        const val IS_INNER = 1
        const val IS_ORDER = -1
    }
}