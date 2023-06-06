package com.vereskul.tc51versusxml.presentation.ui.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereskul.tc51versusxml.domain.models.SupplierOrderModel

class OrderViewPagerAdapter(fragmentManager: FragmentManager,
                            lifecycle: Lifecycle,
                            private val supplierOrderModel: SupplierOrderModel):
    FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return TABS_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> OrderHeaderFragment.newInstance(supplierOrderModel)
            1 -> GoodsFragment.newInstance(supplierOrderModel)
            else -> OrderHeaderFragment.newInstance(supplierOrderModel)
        }
    }

    companion object{
        private const val TABS_COUNT = 2
    }
}