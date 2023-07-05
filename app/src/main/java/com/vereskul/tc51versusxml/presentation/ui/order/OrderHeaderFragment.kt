package com.vereskul.tc51versusxml.presentation.ui.order

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentOrderHeaderBinding
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel


class OrderHeaderFragment : Fragment() {
    private val viewModel by activityViewModels<OrderViewModel>()

    private var _binding: FragmentOrderHeaderBinding? = null
    private val binding: FragmentOrderHeaderBinding
        get()=_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var order: SupplierOrderModel?
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                order = it.getParcelable(OrderHeaderFragment.ARG_PARAM1, SupplierOrderModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                order = it.getParcelable(OrderHeaderFragment.ARG_PARAM1)
            }
            viewModel.setCurrentOrder(order?:throw RuntimeException("Order is not chosen!!!"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_order_header,
            container,
            false
        )

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(param1: SupplierOrderModel) =
            OrderHeaderFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}