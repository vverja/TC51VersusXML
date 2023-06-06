package com.vereskul.tc51versusxml.presentation.ui.order

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vereskul.tc51versusxml.databinding.FragmentGoodsBinding
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class GoodsFragment : Fragment() {

    private var order: SupplierOrderModel? = null

    private var _binding: FragmentGoodsBinding? = null
    private val binding: FragmentGoodsBinding
        get()=_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                order = it.getParcelable(GoodsFragment.ARG_PARAM1, SupplierOrderModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                order = it.getParcelable(GoodsFragment.ARG_PARAM1)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGoodsBinding.inflate(layoutInflater)

        binding.goodsPager.adapter = GoodItemFragmentState(
            parentFragmentManager,
            lifecycle,
            order?.goods?: emptyList()
        )
        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(param1: SupplierOrderModel) =
            GoodsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}