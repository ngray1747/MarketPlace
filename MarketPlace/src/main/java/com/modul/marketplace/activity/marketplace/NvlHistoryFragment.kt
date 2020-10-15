package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.modul.marketplace.adapter.marketplace.HistoryNvlRecyleAdapter
import  com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.openActivity
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.activity.marketplace.NvlHistoryDetailActivity
import com.modul.marketplace.activity.order_online.OrderDetailFragment
import com.modul.marketplace.model.marketplace.NvlOnlineModel
import  com.modul.marketplace.restful.WSRestFull
import  com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_history_order_detail.*
import java.util.*

class NvlHistoryFragment : BaseFragment() {

    private val mDatas = ArrayList<NvlOnlineModel>()
    private var mAdapter: HistoryNvlRecyleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_order_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
        initData()
        initClick()
    }

    private fun initClick() {
        refresh_layout.setOnRefreshListener { orderOnline() }
    }

    private fun initData() {}

    private fun initAdapter() {
        list_order?.apply {
            layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
            mAdapter = HistoryNvlRecyleAdapter(mActivity, mDatas) { dmOrderOnline ->
                var bundle = Bundle()
                bundle.putString(Constants.OBJECT, dmOrderOnline.id)
                bundle.putString(Constants.DATA, OrderDetailFragment.TYPE_ORDER_HISTORY)
                openActivity(NvlHistoryDetailActivity::class.java, bundle)
            }
            adapter = mAdapter
        }
    }

    private fun orderOnline() {
        refresh_layout.isRefreshing = false
        showProgressHub(mActivity)
        WSRestFull(context).apiSCMInvoicesHistory(mCartBussiness.companyId, { (data) -> onResponseOrderOnline(data) }) { error ->
            onResponseOrderOnline(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseOrderOnline(response: ArrayList<NvlOnlineModel>?) {
        dismissProgressHub()
        if (response != null) {
            mDatas.clear()
            mDatas.addAll(response)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        orderOnline()
    }

    companion object {
        fun newInstance(): NvlHistoryFragment {
            return NvlHistoryFragment()
        }
    }
}