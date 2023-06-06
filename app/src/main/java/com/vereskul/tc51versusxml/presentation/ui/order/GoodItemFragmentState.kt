package com.vereskul.tc51versusxml.presentation.ui.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereskul.tc51versusxml.domain.models.GoodsModel

class GoodItemFragmentState(
    fragmentManager: FragmentManager, lifecycle: Lifecycle,
    private val goodsList: List<GoodsModel>
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return goodsList.size
    }

    override fun createFragment(position: Int): Fragment {
        val goodsModel = goodsList[position]
        return GoodsItemFragment.newInstance(goodsModel)
    }
}