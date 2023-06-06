package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.vereskul.tc51versusxml.databinding.FragmentOnlineOrderBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OnlineOrderFragment : Fragment() {
    private val viewModel: OnlineOrderViewModel by activityViewModels()

    private var _binding: FragmentOnlineOrderBinding? = null
    private val binding: FragmentOnlineOrderBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillViewModelData()
        setObservers(view)
        setDateTimePicker(view)
        setClickListeners()
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setTabLayout() {
        val tabLayout = binding.onlineOrderTablayout
        val pager = binding.onlineGoodsPager
        TabLayoutMediator(tabLayout, pager){
            tab, position ->  tab.text = "${position+1}"
        }.attach()
    }

    private fun setDateTimePicker(view: View) {
        val dateTime = LocalDateTime.now()
        binding.orderDate.setText(dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        binding.orderDate.setOnClickListener {
            DatePickerDialog(view.context,{_, year, month, day ->
                val dayStr = day.toString().padStart(2, '0')
                val monthStr = month.toString().padStart(2, '0')
                binding.orderDate.setText("$dayStr.$monthStr.$year")
            }, dateTime.year,dateTime.monthValue, dateTime.dayOfMonth).show()
        }
    }

    private fun fillViewModelData() {
        viewModel.getSuppliers()
        viewModel.getStocks()
        viewModel.getItems()
    }

    private fun setObservers(view: View) {
         viewModel.stocksList.observe(viewLifecycleOwner) {
            ArrayAdapter(
                view.context,
                android.R.layout.simple_dropdown_item_1line, it?.toMutableList() ?: emptyList()
            ).also { adapter ->
                binding.orderStockSelector.setAdapter(adapter)
            }
        }
        viewModel.suppliersList.observe(viewLifecycleOwner) {
            ArrayAdapter(
                view.context,
                android.R.layout.simple_dropdown_item_1line, it?.toMutableList() ?: emptyList()
            ).also { adapter ->
                binding.orderSupplierSelector.setAdapter(adapter)
            }

        }
        viewModel.currentGoodsList.observe(viewLifecycleOwner) {
            binding.onlineGoodsPager.adapter = OnlineOrderGoodsItemFragmentState(
                parentFragmentManager,
                lifecycle,
                it
            )
            setTabLayout()
            val itemCount = (binding.onlineGoodsPager.adapter as OnlineOrderGoodsItemFragmentState)
                .itemCount
            binding.onlineGoodsPager.currentItem = itemCount-1
        }
        viewModel.totalSum.observe(viewLifecycleOwner){
            binding.orderSum.text = it.toString()
        }
    }

    private fun setClickListeners(){
        binding.floatingAbAddItem.setOnClickListener {
            viewModel.addEmptyGoodsItem()
            binding.onlineGoodsPager.currentItem = binding.onlineOrderTablayout.tabCount - 1
        }
    }

    companion object {
        const val TAG = "OnlineOrderFragment"
        @JvmStatic
        fun newInstance() =OnlineOrderFragment()
    }
}