package com.modul.marketplace.activity.marketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.modul.marketplace.adapter.marketplace.MyArticlesListAdapter
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.app.Constants
import com.modul.marketplace.app.Constants.ArticlesStatus.expired
import com.modul.marketplace.app.Constants.ArticlesStatus.selling
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.gone
import com.modul.marketplace.extension.openActivity
import com.modul.marketplace.extension.visible
import com.modul.marketplace.model.marketplace.ArticlesModel
import com.modul.marketplace.model.marketplace.ArticlesModelData
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.DateTimeUtil
import kotlinx.android.synthetic.main.fragment_my_articles.*
import java.util.*

class MyArticlesListFragment(var putStatus: String) : BaseFragment() {

    private var mAdapter: MyArticlesListAdapter? = null
    private val mDatas = ArrayList<ArticlesModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_articles, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
        initData()
        initClick()
        api()
    }

    private fun api() {
        showProgressHub(mActivity)
        var articlesMode = ArticlesModel()
        articlesMode.brand_id = mCartBussiness.getListBrandId()
        articlesMode.company_id = mCartBussiness.companyId
        articlesMode.author_id = mCartBussiness.userId
        articlesMode.status = putStatus
        if (Constants.ArticlesStatus.EXPIRED == putStatus) {
            articlesMode.status =Constants.ArticlesStatus.CONFIRMED
            articlesMode.data_type = expired
        }

        WSRestFull(context).apiSCMArticlesByStatus(articlesMode,
                { response -> callback(response) },
                { error ->
                    callback(null)
                    error.printStackTrace()
                })
    }

    private fun callback(response: ArticlesModelData?) {
        if (mSwipRefreshLayout != null)
            mSwipRefreshLayout.isRefreshing = false
        dismissProgressHub()
        mDatas.clear()
        response?.run {
            response.data?.forEach {
                it.dateName = StringExt.isTextEmpty(DateTimeUtil.convertTimeStampToDate(it.created_at, Constants.Date.Format.HH_MM_DD_MM_YYYY))
                mDatas.add(it)
            }
        }
        if (mError != null) {
            if (mDatas.size == 0) {
                mError.visible()
            } else {
                mError.gone()
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun initClick() {
        mSwipRefreshLayout.setOnRefreshListener {
            api()
        }
    }

    private fun initData() {

    }

    private fun initAdapter() {
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            mAdapter = MyArticlesListAdapter(mActivity, mDatas) {
                    var bundle = Bundle()
                    bundle.putSerializable(Constants.OBJECT,it.id)
                    openActivity(ArticleCreateActivity::class.java,bundle)
            }
            mRecyclerView.adapter = mAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    companion object {
        fun newInstance(putStatus: String): MyArticlesListFragment {
            return MyArticlesListFragment(putStatus)
        }
    }
}