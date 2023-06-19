package com.vereskul.tc51versusxml.presentation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.ActivityMainBinding
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.presentation.ui.online_order.OnlineOrderActivity
import com.vereskul.tc51versusxml.presentation.ui.order.OrderActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var listAdapter: OrdersListAdapter

    private val viewModel: SupplierOrdersViewModel by lazy {
        ViewModelProvider(this,
            SupplierOrdersViewModel.Factory(application))[SupplierOrdersViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listAdapter = OrdersListAdapter()
        binding.ordersList.adapter = listAdapter

        viewModel.orders.observe(this) { list->
            listAdapter.submitList(list)
            listAdapter.onOrderClickListener = { order ->
                val newIntent = OrderActivity.getNewIntent(this, order)
                startActivity(newIntent)
            }
        }
        setupClickListeners()
    }

    private fun setupClickListeners(){
        binding.bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.item_all -> {
                    viewModel.getAllOrders()
                    true
                }
                R.id.item_new ->{
                    viewModel.getNewOrders()
                    true
                }
                R.id.item_in_work ->{
                    viewModel.getInWorkOrders()
                    true
                }
                R.id.item_closed ->{
                    viewModel.getInStockOrders()
                    true
                }
                R.id.item_errors -> {
                    viewModel.getErrorOrders()
                    true
                }
                else -> false
            }
        }
        binding.makeOnlineOrderButton.setOnClickListener {
            val intent = OnlineOrderActivity.newInstance(this)
            startActivity(intent)
            viewModel.getAllOrders()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}