package com.modul.marketplace.activity.order_online

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.activity.CateActivity
import com.modul.marketplace.adapter.orderonline.HistoryOrderOnlineRecyleAdapter
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.orderonline.DmHistoryOrderDetail
import com.modul.marketplace.model.orderonline.DmOrderOnline
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_history_order_detail.*
import java.util.*

class HistoryOrderDetailFragment : BaseFragment() {
    private val mDatas = ArrayList<DmOrderOnline>()
    private var mAdapter: HistoryOrderOnlineRecyleAdapter? = null

    private var page = 1
    private var results_per_page = 10
    private var isLoading = false
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_order_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
        initData()
        initClick()
        initDataLoad()
        initApi(page, results_per_page)
    }

    private fun initClick() {
        mSwipRefreshLayout?.setOnRefreshListener {
            page = 1
            mDatas.clear()
            initApi(page, results_per_page)
        }
    }

    private fun getPage() {
        isLoading = true
        pbLoading.visibility = View.VISIBLE
        Handler().postDelayed({
            initApi(page, results_per_page)
            isLoading = false
            pbLoading.visibility = View.GONE
        }, 500)
    }

    private fun initDataLoad() {
        layoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val pastVisibleItem: Int =
                            layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total: Int? = mAdapter?.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) > total!!) {
                            page += 1
                            getPage()
                        }
                    }
                }
            }
        })
    }

    private fun initApi(page: Int, resultsPerPage: Int) {
        mSwipRefreshLayout?.apply {
            isRefreshing = true
        }
        WSRestFull(context).apiOrderHistoryHermes(mCartBussiness.companyId,page, { response -> onResponseOrderOnline(response.data) }) { error ->
            onResponseOrderOnline(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun initData() {}

    private fun initAdapter() {
        mAdapter = HistoryOrderOnlineRecyleAdapter(mActivity, mDatas) { dmOrderOnline: DmOrderOnline -> CateActivity.gotoOrderDetailFragment(mActivity, dmOrderOnline.orderCode, OrderDetailFragment.TYPE_ORDER_HISTORY) }
        mRecyclerView.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
    }

    private fun onResponseOrderOnline(response: DmHistoryOrderDetail?) {
        mSwipRefreshLayout?.apply {
            isRefreshing = false
        }
        response?.run {
           getmDatas().forEach {
               mDatas.add(it)
            }
        }

        if(mError != null){
            if (mDatas.size == 0) {
                mError.visible()
            } else {
                mError.gone()
            }
        }
        mAdapter?.notifyDataSetChanged()
    }


    companion object {
        @JvmStatic
        fun newInstance(): HistoryOrderDetailFragment {
            return HistoryOrderDetailFragment()
        }
    }
}