package com.modul.marketplace.activity.marketplace

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.modul.marketplace.adapter.marketplace.SelectAddressAdapter
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.openActivityForResult
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.model.marketplace.AddressModelData
import com.modul.marketplace.model.marketplace.LocationModel
import com.modul.marketplace.model.marketplace.LocationModelData
import com.modul.marketplace.model.marketplace.LocationModelDataObject
import com.modul.marketplace.model.orderonline.DmDeliveryInfo
import com.modul.marketplace.restful.ApiRequest
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.ToastUtil
import kotlinx.android.synthetic.main.activity_select_address.*
import kotlinx.android.synthetic.main.include_header2.*

class SelectAddressOrderNvlActivity : BaseActivity() {

    val REQUEST_CODE_PICK_LOCATION = 1001
    private var mAdapter: SelectAddressAdapter? = null
    private val mDatas = ArrayList<LocationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_address)
        initAdapter()
        initData()
        initClick()
        api()
    }

    private fun api() {
        showProgressHub(this)
        val callback: ApiRequest<LocationModelData> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMLocation(1,mCartBussiness.companyId,mCartBussiness.getListBrandId(),mCartBussiness.userId),
                { response ->  locationDone(response.data) }) { error ->
            locationDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiXoaLocation(locationModel: LocationModel) {
        showProgressHub(this)
        val callback: ApiRequest<LocationModelDataObject> = ApiRequest()
        callback.setCallBack(mApiSCM?.apiSCMLocationDelete(locationModel.id),
                { response ->  deleteDone(response.data) }) { error ->
            deleteDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun deleteDone(data: LocationModel?) {
        dismissProgressHub()
        data?.run{
            api()
        }
    }

    private fun locationDone(data: ArrayList<LocationModel>?) {
        dismissProgressHub()
        mDatas.clear()
        data?.run {
            forEach {
                mCartBussiness.getOrder()?.dmDeliveryInfo?.locationUid?.run{
                    if(it.id == this){
                        it.selected = true
                    }
                }
                mDatas.add(it)
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun initAdapter() {
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@SelectAddressOrderNvlActivity, RecyclerView.VERTICAL, false)
            mAdapter = SelectAddressAdapter(this@SelectAddressOrderNvlActivity, mDatas, itemClickListener = {
                mDatas.forEach { item ->
                    item.selected = false
                }
                it.selected = true
                mCartBussiness.getOrder().dmDeliveryInfo = DmDeliveryInfo()
                mCartBussiness.getOrder().dmDeliveryInfo.address = it.location_name
                mCartBussiness.getOrder().dmDeliveryInfo.locationUid = it.id
                mCartBussiness.getOrder().dmDeliveryInfo.city = it.city?.fb_id
                mCartBussiness.getOrder().dmDeliveryInfo.district = it.district?.fb_id

                mAdapter?.notifyDataSetChanged()
                val i = Intent()
                setResult(RESULT_OK, i)
            }, itemEdit = {
                var bundle = Bundle()
                bundle.putSerializable(Constants.OBJECT,it)
                openActivityForResult(AddAddressOrderNvlActivity::class.java,bundle = bundle,requestCode = REQUEST_CODE_PICK_LOCATION)
            }, itemDelete = {
                apiXoaLocation(it)
            }, object : SelectAddressAdapter.OnSwipeItem {
                override fun onSwipeLeft(item: LocationModel) {}
                override fun onSwipeRight(item: LocationModel) {}
                override fun onSwipeTop(item: LocationModel) {}
                override fun onSwipeBottom(item: LocationModel) {}
                override fun onClickItem(item: LocationModel) {}
            })
//            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mAddAddress.setOnClickListener { addAddress() }
    }

    private fun addAddress() {
        openActivityForResult(AddAddressOrderNvlActivity::class.java,REQUEST_CODE_PICK_LOCATION)
    }

    private fun initData() {
        showStatusBar(color = R.color.grayF8, statusColor = true)
        mlbTitle.text = getString(R.string.chon_dia_chi)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                api()
            }
        }
    }
}