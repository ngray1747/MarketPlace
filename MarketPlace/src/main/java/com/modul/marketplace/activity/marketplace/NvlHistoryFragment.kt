package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import android.os.Handler
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
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.marketplace.ArticlesModelData
import com.modul.marketplace.model.marketplace.NvlOnlineModel
import com.modul.marketplace.model.marketplace.NvlOnlineModelDataList
import com.modul.marketplace.model.orderonline.DmHistoryOrderDetail
import com.modul.marketplace.restful.ApiRequest
import  com.modul.marketplace.restful.WSRestFull
import  com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_history_order_detail.*
import java.util.*

class NvlHistoryFragment : BaseFragment() {

    private val mDatas = ArrayList<NvlOnlineModel>()
    private var mAdapter: HistoryNvlRecyleAdapter? = null

    private var page = 1
    private var results_per_page = 10
    private var isLoading = false
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_order_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
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

    private fun initAdapter() {
        mRecyclerView?.apply {
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

    private fun initApi(page: Int, resultsPerPage: Int) {
        mSwipRefreshLayout?.apply {
            isRefreshing = true
        }

        val callback: ApiRequest<NvlOnlineModelDataList> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMInvoicesHistory(mCartBussiness.companyId,mCartBussiness.userId,page,10),
                { response ->  onResponseOrderOnline(response.data) }) { error ->
            onResponseOrderOnline(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseOrderOnline(response: ArrayList<NvlOnlineModel>?) {
        mSwipRefreshLayout?.apply {
            isRefreshing = false
        }

        response?.run {
            forEach {
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
        fun newInstance(): NvlHistoryFragment {
            return NvlHistoryFragment()
        }
    }
}