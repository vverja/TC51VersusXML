package com.vereskul.tc51versusxml.presentation.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vereskul.tc51versusxml.R
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

    class OrderListViewHolder(private val binding: OrdersListItemBinding):ViewHolder(binding.root){
        fun bind(
            supplierOrderModel: SupplierOrderModel,
            onOrderClickListener: ((SupplierOrderModel)->Unit)?){
            val orderNumber = binding.textNumber
            val orderSupplier = binding.textSupplier
            val orderDate = binding.textDate
            val orderImg = binding.imageOrderState
            binding.root.setOnClickListener {
                onOrderClickListener?.invoke(supplierOrderModel)
            }
            with(supplierOrderModel){
                orderNumber.text = number
                orderSupplier.text = supplier
                orderDate.text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                var circleId = when(orderState){
                    OrderStatus.NEW -> R.drawable.green_circle
                    OrderStatus.IN_WORK -> R.drawable.yellow_circle
                    OrderStatus.CANCELED -> R.drawable.multiply
                    OrderStatus.IN_STOCK ->R.drawable.blue_circle
                    else -> throw RuntimeException("unknown order status")
                    }
                    orderImg.background = ContextCompat.getDrawable(binding.root.context, circleId)
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
        val inflate =
            OrdersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return OrderListViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order,onOrderClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}