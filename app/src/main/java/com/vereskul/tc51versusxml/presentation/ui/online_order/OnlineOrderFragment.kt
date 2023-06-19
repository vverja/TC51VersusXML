package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentOnlineOrderBinding
import com.vereskul.tc51versusxml.domain.models.StockModel
import com.vereskul.tc51versusxml.domain.models.SupplierModel
import com.vereskul.tc51versusxml.presentation.ui.login.afterTextChanged
import kotlinx.coroutines.launch
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
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_online_order,
            container,
            false
        )
        //_binding = FragmentOnlineOrderBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillViewModelData()

        setObservers(view)

        setDateTimePicker(view)
        setClickListeners()
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
        binding.orderDate.setOnClickListener {

            DatePickerDialog(view.context,{_, year, month, day ->
               val localDateTime = LocalDateTime.of(
                   year,
                   month + 1,
                   day,
                   dateTime.hour,
                   dateTime.minute,
                   dateTime.second
               )
                binding.orderDate.setText(
                    localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                )

            }, dateTime.year,dateTime.monthValue-1, dateTime.dayOfMonth).show()
        }
    }

    private fun fillViewModelData() {
        viewModel.getSuppliers()
        viewModel.getStocks()
        viewModel.getItems()
    }

    private fun setObservers(view: View) {

        viewModel.suppliersList.observe(viewLifecycleOwner) {
            ArrayAdapter(
                view.context,
                android.R.layout.simple_dropdown_item_1line, it?.toMutableList() ?: emptyList()
            ).also { adapter ->
                binding.orderSupplierSelector.setAdapter(adapter)
            }

        }
        binding.orderSupplierSelector.setOnItemClickListener { parent, _, position, _ ->
            viewModel.selectedSupplier = parent.adapter.getItem(position) as SupplierModel
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

        textChangeListeners()

        saveButtonObserver()

        formStateObserver()
    }

    private fun formStateObserver() = lifecycleScope.launch {
        viewModel.formState.collect{ formState ->
            with(binding){
                formState.supplierError?.let {
                    orderSupplierSelector.error = getString(it)
                }
                formState.dateError?.let {
                    orderDate.error = getString(it)
                }
                sendOrder.isEnabled = formState.isDataValid
            }
        }
    }

    private fun saveButtonObserver() = lifecycleScope.launch {
            viewModel.saveResult.collect { saveResult ->
                if (saveResult.success != null) {
                    val snackbar = Snackbar.make(
                        binding.mainOnlineOrderLayout,
                        saveResult.success,
                        Snackbar.LENGTH_SHORT
                    ).setDuration(1000)

                    snackbar.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            activity?.finish()
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                        }
                    })
                    snackbar.show()
                } else if (saveResult.error != null) {
                    Snackbar.make(
                        binding.mainOnlineOrderLayout,
                        saveResult.error,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }


    private fun textChangeListeners() {
        binding.orderSupplierSelector.afterTextChanged {
            viewModel.orderDataChanged()
        }

        binding.orderDate.afterTextChanged {
            viewModel.orderDataChanged()
        }

    }

    private fun setClickListeners(){
        binding.floatingAbAddItem.setOnClickListener {
            viewModel.addEmptyGoodsItem()
            binding.onlineGoodsPager.currentItem = binding.onlineOrderTablayout.tabCount - 1
        }
        binding.sendOrder.setOnClickListener {
            viewModel.saveOrder()
        }
    }

    companion object {
        const val TAG = "OnlineOrderFragment"
        fun newInstance() =OnlineOrderFragment()
    }
}