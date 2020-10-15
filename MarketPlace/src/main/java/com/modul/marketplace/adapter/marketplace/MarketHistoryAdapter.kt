package com.modul.marketplace.adapter.marketplace

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.modul.marketplace.activity.marketplace.NvlHistoryFragment
import com.modul.marketplace.activity.order_online.HistoryOrderDetailFragment

class MarketHistoryAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val roleSale by lazy {
        arrayListOf(
                HistoryOrderDetailFragment.newInstance(),
                NvlHistoryFragment.newInstance()
        )
    }

    override fun getItemCount(): Int {
        return roleSale.size
    }

    override fun createFragment(position: Int): Fragment {
        return roleSale[position]
    }
}