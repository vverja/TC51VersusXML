package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vereskul.tc51versusxml.databinding.FragmentOnlineOrderGoodsItemBinding
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OnlineOrderGoodsItemFragment : Fragment() {
    private val viewModel: OnlineOrderViewModel by activityViewModels()
    private var goodsModel: GoodsModel? = null
    private var _binding: FragmentOnlineOrderGoodsItemBinding? = null
    private val binding: FragmentOnlineOrderGoodsItemBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                goodsModel = it.getParcelable(OnlineOrderGoodsItemFragment.GOODS_MODEL_PARAM, GoodsModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                goodsModel = it.getParcelable(OnlineOrderGoodsItemFragment.GOODS_MODEL_PARAM)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineOrderGoodsItemBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsList.observe(viewLifecycleOwner){
            ArrayAdapter(
                view.context,
                android.R.layout.simple_dropdown_item_1line,
                it?.map {item ->
                    item.name}?: emptyList()
            ).also {adapter ->
                binding.selectItem.setAdapter(adapter)
            }

            formStateObserver()
        }

        binding.selectItem.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                parent?.let {
                    val itemByName =
                        viewModel.getItemByName(it.getItemAtPosition(position) as String)
                    binding.currentUnits.text = itemByName?.units
                    binding.selectBarcode.setText(itemByName?.barcode)

                }
            }

    }

    private fun formStateObserver() = lifecycleScope.launch {
        viewModel.formState.collect{ formState ->
            formState.goodsListError?.let {
                Snackbar.make(binding.root, getString(it), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val GOODS_MODEL_PARAM = "param1"
        private const val TAG = "OnlineOrderGoodsItemFragment"
        @JvmStatic
        fun newInstance(param1: GoodsModel) =
            OnlineOrderGoodsItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(GOODS_MODEL_PARAM, param1)
                }
            }
    }
}