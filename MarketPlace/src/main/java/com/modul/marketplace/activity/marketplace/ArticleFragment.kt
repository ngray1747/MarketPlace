package com.modul.marketplace.activity.marketplace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.adapter.marketplace.ArtilesAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.DialogUtil
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.openActivity
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.model.marketplace.ArticlesModelData
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.fragment_article.*
import java.util.*

class ArticleFragment : BaseFragment() {
    private var mAdapter: ArtilesAdapter? = null
    private val mDatas = ArrayList<ArticlesModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(onNotice, IntentFilter(Constants.BROADCAST.BROAD_ARTICLES))
        }
        initAdapter()
        initData()
        initClick()
    }

    private fun initClick() {
        mCreate.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(mCartBussiness.companyId)) {
                context?.let {
                    DialogUtil.showAlert(it, textTitle = R.string.thongbao, textMessage = R.string.article_companyId_valid, textCancel = R.string.desau, textOk = R.string.lien_he, okListener = {
                       Utilities.callPhone(activity,"19004766")
                    })
                }
            } else {
                openActivity(ArticleCreateActivity::class.java)
            }
        }
    }

    private fun initData() {
        Utilities.sendBoardCounlyLib(context, Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.BROWSER_ARTICLE)
        callServiceList()
    }

    private fun initAdapter() {
        listRecyle?.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            mAdapter = ArtilesAdapter(context, mDatas) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.OBJECT, it)
                openActivity(
                        ArticleDetailActivity::class.java,
                        bundle = bundle
                )
            }
            adapter = mAdapter
        }
    }

    private fun callServiceList() {
        showProgressHub(mActivity)
        val callback: ApiRequest<ArticlesModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMArticles(mCartBussiness.getCartLocate().locateId, mCartBussiness.companyId, mCartBussiness.getListBrandId()),
                { response -> onResponseServiceList(response.data) }) { error ->
            onResponseServiceList(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponseServiceList(data: ArrayList<ArticlesModel>?) {
        dismissProgressHub()
        mDatas.clear()
        if (data != null) {
            mDatas.addAll(data)
        }

        if (mLoi != null) {
            if (mDatas.size == 0) {
                mLoi.visible()
            } else {
                mLoi.gone()
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(onNotice)
        }
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            callServiceList()
        }
    }

    companion object {
        fun newInstance(): ArticleFragment {
            return ArticleFragment()
        }
    }
}