package com.vereskul.tc51versusxml.presentation.ui.order

import com.vereskul.tc51versusxml.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.vereskul.tc51versusxml.databinding.ActivityOrderBinding
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel


class OrderActivity : AppCompatActivity() {

    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)

    }


    private var order: SupplierOrderModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.orderTablayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            order = intent.getParcelableExtra(ORDER,SupplierOrderModel::class.java)
        }else{
            @Suppress("DEPRECATION")
            order = intent.getParcelableExtra(ORDER)
        }

        viewPager.adapter = OrderViewPagerAdapter(supportFragmentManager, lifecycle, order!!)

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            if (position==0)
                tab.text = getString(R.string.order)
            else
                tab.text = getString(R.string.goods)
        }.attach()
        registerBroadcastReceiver()
    }
        fun registerBroadcastReceiver(){
            val filter = IntentFilter()
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            filter.addAction(resources.getString(R.string.activity_intent_filter_action))
            registerReceiver(barcodeReceiver, filter)
        }

    private val barcodeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action.equals(getString(R.string.activity_intent_filter_action))){
                val decodedSource = intent.getStringExtra(
                        getString(R.string.datawedge_intent_key_source)
                ).toString()
                Log.d(TAG,"decodedSource: $decodedSource")
                val decodedData = intent.getStringExtra(
                    getString(R.string.datawedge_intent_key_data)
                ).toString()
                Log.d(TAG,"decodedData: $decodedData")
                val decodedLabelType = intent.getStringExtra(
                    getString(R.string.datawedge_intent_key_label_type)
                ).toString()
                Log.d(TAG,"decodedLabelType: $decodedLabelType")
            }
        }
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