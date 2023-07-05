package com.vereskul.tc51versusxml.presentation.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.ActivityMainBinding
import com.vereskul.tc51versusxml.presentation.ui.online_order.OnlineOrderActivity
import com.vereskul.tc51versusxml.presentation.ui.order.OrderActivity
import kotlinx.coroutines.launch

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


        setupClickListeners()
        setObservers()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refreshFromRepository()
    }

    private fun setObservers() {
        viewModel.orders.observe(this) { list->
            listAdapter.submitList(list)
            listAdapter.onOrderClickListener = { order ->
                val newIntent = OrderActivity.getNewIntent(this, order)
                startActivity(newIntent)
            }
            viewModel.countNewOrders()
        }

        lifecycleScope.launch {
            viewModel.onlyInnerOrders.collect{
                viewModel.getAllOrders()
            }
        }

        viewModel.countNewOrders.observe(this){pair ->
            if(pair.first > 0) {
                binding.badgeIncome.visibility = View.VISIBLE
                binding.badgeIncome.text = pair.first.toString()
            }else{
                binding.badgeIncome.visibility = View.GONE
            }
            if (pair.second>0) {
                binding.badgeOutgo.visibility = View.VISIBLE
                binding.badgeOutgo.text = pair.second.toString()
            }else{
                binding.badgeOutgo.visibility = View.GONE
            }
        }
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
                R.id.refresh_orders ->{
                    viewModel.refreshFromRepository()
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
        binding.incomeButton.setOnClickListener {
            viewModel.isInnerOrders(false)
        }
        binding.outgoButton.setOnClickListener {
            viewModel.isInnerOrders(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}