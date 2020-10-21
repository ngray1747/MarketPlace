package com.modul.marketplace.activity.order_online

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseFragment
import com.modul.marketplace.activity.CateActivity
import com.modul.marketplace.adapter.orderonline.CartRecyleAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.DialogUtil
import com.modul.marketplace.model.orderonline.DmService
import com.modul.marketplace.model.orderonline.DmVoucher
import com.modul.marketplace.paser.orderonline.RestAllDmCheckAutoPromotion
import com.modul.marketplace.paser.orderonline.RestAllDmCheckVoucher
import com.modul.marketplace.paser.orderonline.RestAllDmCheckVoucherToServer
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.DialogYesNo
import com.modul.marketplace.util.FormatNumberUtil
import com.modul.marketplace.util.Log
import com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.include_header2.*
import java.util.*

class CartFragment : BaseFragment() {
    private var mAdapter: CartRecyleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                    .registerReceiver(onNotice, IntentFilter(Constants.BROADCAST.BROAD_CART))
        }
        initData()
        initClick()
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { v: View? -> mActivity.onBackPressed() }
        payment.setOnClickListener { v1: View? -> CateActivity.gotoInformationFragment(mActivity) }
        input_voucher.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 0) {
                    check_voucher.isEnabled = true
                    check_voucher.alpha = 1f
                } else {
                    check_voucher.isEnabled = false
                    check_voucher.alpha = 0.5f
                }
            }
        })
        check_voucher.setOnClickListener { v1: View? ->
            if (mCartBussiness.getOrder().haveAutoPromotion) {
                showDialogCheckPromotion()
            } else {
                addVoucher()
            }
        }
    }

    private fun showDialogCheckPromotion() {
        val dialogYesNo: DialogYesNo = object : DialogYesNo(mActivity) {
            override val header: String?
                get() = getString(R.string.thongbao)
            override val message: String?
                get() = getString(R.string.mess_apply_voucher)

            override fun setActionYes() {
                addVoucher()
                dismiss()
            }

            override fun setActionNo() {
                dismiss()
            }
        }
        dialogYesNo.show()
    }

    private fun addVoucher() {
        val voucher = input_voucher.text.toString()
        if (!TextUtils.isEmpty(voucher)) {
            checkVoucherCode(voucher)
        }
    }

    private fun checkVoucherCode(voucherCode: String) {
        loadData()
        showProgressHub(mActivity)
        val restAllDmCheckVoucher = RestAllDmCheckVoucherToServer()
        restAllDmCheckVoucher.objects = mCartBussiness.getOrder().details
        restAllDmCheckVoucher.voucherCode = voucherCode
        WSRestFull(context).apiCheckVoucher(restAllDmCheckVoucher.toJson(), { response: RestAllDmCheckVoucher ->
            dismissProgressHub()
            onReponseCheckVoucher(response.data)
        }) { error: VolleyError ->
            dismissProgressHub()
            onReponseCheckVoucher(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onReponseCheckVoucher(dmVoucher: DmVoucher?) {
        if (dmVoucher != null) {
            voucher_name.visibility = View.VISIBLE
            mCartBussiness.getOrder().dmVoucher = dmVoucher
            voucher_name.text = mCartBussiness.getOrder().dmVoucher.promoCampaignName
            if (DmVoucher.TYPE_DISCOUNT == dmVoucher.type) {
                mCartBussiness.getOrder().discountAmount = dmVoucher.usedDiscountAmount
                voucher_amount.text = "-" + FormatNumberUtil.formatCurrency(mCartBussiness.getOrder().discountAmount)
            } else {
                val gitfs = dmVoucher.gifts
                if (gitfs != null) {
                    for (dmService in gitfs) {
                        dmService.position = mCartBussiness.getOrder().details.size + 1
                        mCartBussiness.getOrder().details.add(dmService)
                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }
            validAmountCart()
        }
    }

    private fun checkAutoPromotion() {
        loadData()
        showProgressHub(mActivity)
        val restAllDmCheckAutoPromotion = RestAllDmCheckAutoPromotion()
        restAllDmCheckAutoPromotion.datas = mCartBussiness.getOrder().details
        Log.e("rest All", "auto Promotion: " + restAllDmCheckAutoPromotion.toJson())
        WSRestFull(context).apiCheckAutoPromotion(restAllDmCheckAutoPromotion.toJson(), { response: RestAllDmCheckAutoPromotion ->
            dismissProgressHub()
            onResponePromotion(response.datas)
        }) { error: VolleyError ->
            dismissProgressHub()
            onResponePromotion(null)
            error.printStackTrace()
            ToastUtil.makeText(mActivity, getString(R.string.error_network2))
        }
    }

    private fun onResponePromotion(reponse: ArrayList<DmService>?) {
        if (reponse != null) {
            mCartBussiness.OrderOnlineAutoPromotionToDetail(reponse)
            validAmountCart()
        }
        initAdapter()
    }

    private fun initData() {
        mlbTitle.text = resources.getString(R.string.cart)
        checkAutoPromotion()
    }

    private fun initAdapter() {
        listRecyle?.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            mAdapter = CartRecyleAdapter(mActivity, mCartBussiness.getOrder().cart) {
                DialogUtil.showAlertEdit(context = context, dmService = it, okListener = {
                    mCartBussiness.OrderOnlineReCheckDataOrigin(it)
                    loadData()
                    if (mCartBussiness.getOrder().haveAutoPromotion) {
                        checkAutoPromotion()
                    } else {
                        addVoucher()
                    }
                    initAdapter()
                })
            }
            adapter = mAdapter
        }
    }

    private fun loadData() {
        mCartBussiness.OrderOnlineConvertOriginToDetail()
        validAmountCart()
    }

    private fun validAmountCart() {
        provisional?.text = FormatNumberUtil.formatCurrency(mCartBussiness.OrderOnlineAmountItem())
        total_amount?.text = FormatNumberUtil.formatCurrency(mCartBussiness.OrderOnlineTotalAmount())
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(onNotice)
        }
    }

    var onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.getStringExtra("value") == Constants.BROADCAST.BACK){
                mActivity.finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): CartFragment {
            return CartFragment()
        }
    }
}