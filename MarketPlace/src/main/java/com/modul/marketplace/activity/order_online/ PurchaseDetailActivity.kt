package com.modul.marketplace.activity.order_online

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.modul.marketplace.model.orderonline.DmServiceListOrigin
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.initAvatarCompany
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.adapter.orderonline.RowItemAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.orderonline.RowItemModel
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_purchase_detail.*

class PurchaseDetailActivity : BaseActivity() {

    private lateinit var mAdapter: RowItemAdapter
    private var mResult: ArrayList<RowItemModel> = ArrayList()
    private var dmServiceListOrigin: DmServiceListOrigin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_detail)
        initAdapter()
        initExtraValue()
        initData()
        initClick()
    }

    private fun initExtraValue() {
        dmServiceListOrigin = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as DmServiceListOrigin?
            } else {
                null
            }
        }?.copy()

        dmServiceListOrigin?.run {
            initAvatarCompany(baseContext,mImg, image, baseContext.getDrawable(R.drawable.icon_default))
            if(quantity == 0.0){
                mQuantity.text = "1"
                dmServiceListOrigin?.quantity = 1.0
            }else{
                mQuantity.text = "" + StringExt.convertNumberToString(quantity)
            }
            mResult.add(RowItemModel(title = name, isOnlyTitle = true))
            mResult.add(RowItemModel(title = getString(R.string.gia), content = StringExt.convertToMoney(unitPrice) + "/ " + unitName, contentColor = R.color.mainColor, contentStyle = R.style.TextView_SemiBold))
            mResult.add(RowItemModel(title = getString(R.string.description), content = desc))
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun initAdapter() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter = RowItemAdapter(context, mResult) {

            }
            adapter = mAdapter
        }
    }

    private fun initClick() {
        mClose.setOnClickListener { onBackPressed() }
        mMinus.setOnClickListener { minus() }
        mPlus.setOnClickListener { plus() }
        mOrder.setOnClickListener { order() }
    }

    private fun order() {
        Utilities.sendBoardCounlyLib(baseContext,Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.ADD_HERMES_PRODUCT_TO_CART)
        Intent().apply {
            putExtra(Constants.OBJECT, dmServiceListOrigin)
            setResult(Activity.RESULT_OK, this)
            onBackPressed()
        }
    }

    private fun plus() {
        var quantity = mQuantity.text.toString().toInt()
        mQuantity.text = "" + quantity.plus(1)
        dmServiceListOrigin?.quantity = mQuantity.text.toString().toDouble()
    }

    private fun minus() {
        var quantity = mQuantity.text.toString().toInt()
        if (quantity > 0) {
            mQuantity.text = "" + quantity.minus(1)
        }
        dmServiceListOrigin?.quantity = mQuantity.text.toString().toDouble()
    }

    private fun initData() {
        showStatusBar(color = R.color.white, statusColor = true)
        Utilities.sendBoardCounlyLib(baseContext,Constants.BROADCAST.BROAD_MANAGER_HOME_CALLBACK, Constants.BROADCAST.MARKETPLACE_HERMES_COUNTLY, Constants.Countly.EVENT.FEATURE, Constants.Countly.CounlyComponent.MARKET_PLACE, Constants.Countly.CounlyFeature.VIEW_HERMES_PRODUCT_DETAIL)
    }
}