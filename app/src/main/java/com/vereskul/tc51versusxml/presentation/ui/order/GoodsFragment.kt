package com.vereskul.tc51versusxml.presentation.ui.order

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentGoodsBinding
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel


class GoodsFragment : Fragment() {

    private val viewModel: OrderViewModel  by activityViewModels()

    private var _binding: FragmentGoodsBinding? = null
    private val binding: FragmentGoodsBinding
        get()=_binding!!

    private var barcodeReceiverCurrentPage = INITIAL_VALUE
    private var canMakeChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var order: SupplierOrderModel? = null
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                order = it.getParcelable(GoodsFragment.ARG_PARAM1, SupplierOrderModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                order = it.getParcelable(GoodsFragment.ARG_PARAM1)
            }
        }
        viewModel.setCurrentOrder(order?:throw RuntimeException("Order is not chosen!!!"))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGoodsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()

        registerBroadcastReceiver()
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setObservers() {
        viewModel.order.observe(viewLifecycleOwner){order ->
            val fragmentState = GoodItemFragmentState(
                parentFragmentManager,
                lifecycle,
                order?.goods ?: emptyList()
            )
            if(binding.goodsPager.adapter==null){
                binding.goodsPager.adapter = fragmentState
            }else if (barcodeReceiverCurrentPage > INITIAL_VALUE){
                (binding.goodsPager.adapter as GoodItemFragmentState).notifyItemChanged(INITIAL_VALUE)
                barcodeReceiverCurrentPage = INITIAL_VALUE
            }

            canMakeChanges = when (order.orderState) {
                OrderStatus.NEW -> false
                OrderStatus.IN_WORK -> true
                OrderStatus.IN_STOCK -> false
                OrderStatus.CANCELED -> TODO()
                null -> throw RuntimeException("Статус заказа не определен")
            }
        }
    }

    private fun registerBroadcastReceiver(){
        val filter = IntentFilter()
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addAction(resources.getString(R.string.activity_intent_filter_action))
        registerReceiver(requireContext(), barcodeReceiver, filter, RECEIVER_EXPORTED)
    }

    private val barcodeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action.equals(getString(R.string.activity_intent_filter_action))){
                if (!canMakeChanges){
                    val message = if (viewModel.order.value?.orderState == OrderStatus.NEW){
                        getString(R.string.error_work_begin)
                    }else{
                        getString(R.string.error_order_closed)
                    }
                    Snackbar.make(
                        binding.root,
                        message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
                val decodedData = intent.getStringExtra(
                    getString(R.string.datawedge_intent_key_data)
                ).toString()
                Log.d(TAG,"decodedData: $decodedData")

                val currentPage = viewModel.getCurrentPageByBarcode(decodedData) ?: 0
                if(currentPage<0){
                    Snackbar.make(
                        binding.root,
                        getString(R.string.barcode_not_found),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
                if (binding.goodsPager.currentItem != currentPage) {
                    binding.goodsPager.currentItem = currentPage
                }
                barcodeReceiverCurrentPage = currentPage
                viewModel.changeQtyFact(currentPage,1.0)
            }
        }
    }
    companion object {
        private const val TAG = "GoodsFragment"
        private const val ARG_PARAM1 = "param1"
        private const val INITIAL_VALUE = -1
        @JvmStatic
        fun newInstance(param1: SupplierOrderModel) =
            GoodsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}