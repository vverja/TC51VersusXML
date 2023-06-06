package com.vereskul.tc51versusxml.presentation.ui.order

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentGoodsBinding
import com.vereskul.tc51versusxml.databinding.FragmentOrderHeaderBinding
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel
import java.time.format.DateTimeFormatter


class OrderHeaderFragment : Fragment() {
    private var order: SupplierOrderModel? = null

    private var _binding: FragmentOrderHeaderBinding? = null
    private val binding: FragmentOrderHeaderBinding
        get()=_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                order = it.getParcelable(OrderHeaderFragment.ARG_PARAM1, SupplierOrderModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                order = it.getParcelable(OrderHeaderFragment.ARG_PARAM1)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrderHeaderBinding.inflate(layoutInflater)
        with(binding){
            textNumber.text = order?.number
            textDate.text = order?.date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            textSupplier.text= order?.supplier
            textTimeBegin.text = order?.startTime?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"))
            textTimeEnd.text = order?.endTime?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"))
        }

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