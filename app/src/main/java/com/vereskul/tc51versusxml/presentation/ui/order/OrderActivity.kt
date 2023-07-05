package com.vereskul.tc51versusxml.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.ActivityOrderBinding
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel


class OrderActivity : AppCompatActivity() {


    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }

    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var order: SupplierOrderModel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            order = intent.getParcelableExtra(ORDER,SupplierOrderModel::class.java)
        }else{
            @Suppress("DEPRECATION")
            order = intent.getParcelableExtra(ORDER)
        }
        viewModel.setCurrentOrder(order?:throw RuntimeException("Order is not chosen!!!"))

        val viewPager = binding.viewPager
        val tabLayout = binding.orderTablayout
        viewPager.adapter = OrderViewPagerAdapter(supportFragmentManager, lifecycle, order)
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            if (position==0)
                tab.text = getString(R.string.order)
            else
                tab.text = getString(R.string.goods)
        }.attach()
        setOnClickListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.order.observe(this){order ->
            when(order.orderState){
                OrderStatus.NEW ->  binding.buttonEndWork.isEnabled = false
                OrderStatus.IN_WORK -> {
                    binding.buttonBeginWork.isEnabled = false
                    binding.buttonEndWork.isEnabled = true
                }
                OrderStatus.IN_STOCK -> {
                    binding.buttonBeginWork.isEnabled = false
                    binding.buttonEndWork.isEnabled = false
                }
                OrderStatus.CANCELED -> {
                    binding.buttonBeginWork.isEnabled = true
                    binding.buttonEndWork.isEnabled = false
                }
                null -> throw RuntimeException("Статус заказа не определен")
            }
        }

        viewModel.orderFormState.observe(this@OrderActivity) {
            if (it.isDataValid) {
                viewModel.endOrder()
                Snackbar.make(
                    binding.root,
                    getString(R.string.order_is_over),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                var errorId = 0
                if (it.qtyFactError != null) {
                    errorId = it.qtyFactError
                } else if (it.barcodeError != null) {
                    errorId = it.barcodeError
                }
                if (errorId != 0) {
                    alertDialog(errorId) {
                        viewModel.endOrder()
                    }.show()
                }
            }
        }

    }

    private fun setOnClickListeners() {
        binding.buttonBeginWork.setOnClickListener {
            val alertDialog = alertDialog(R.string.begin_work_question) {
                viewModel.beginOrder()
            }
            alertDialog.show()
        }
        binding.buttonEndWork.setOnClickListener {
                viewModel.dataChanged()
        }
        binding.buttonClose.setOnClickListener {
            viewModel.closeOrder()
            finish()
        }
    }

    private fun alertDialog(messageId: Int, block: ()->Unit): AlertDialog {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(messageId)
                setPositiveButton(R.string.ok) { _, _ ->
                    block()
                }
                setNegativeButton(R.string.cancel) { _, _ ->

                }
            }
            builder.create()
        }
        return alertDialog
    }


    companion object{
        private const val TAG = "OrderActivity"
        private const val ORDER = "ORDER"
        fun getNewIntent(context: Context, order: SupplierOrderModel):Intent{
            return Intent(context, OrderActivity::class.java).also {
                it.putExtra(ORDER, order)
            }
        }
    }
}