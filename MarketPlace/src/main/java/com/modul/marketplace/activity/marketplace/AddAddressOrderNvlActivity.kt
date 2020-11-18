package com.modul.marketplace.activity.marketplace

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.VolleyError
import com.modul.marketplace.R
import com.modul.marketplace.activity.BaseActivity
import com.modul.marketplace.activity.order_online.InformationFragment
import com.modul.marketplace.activity.order_online.MapActivity
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.showStatusBar
import com.modul.marketplace.extension.showToast
import com.modul.marketplace.model.marketplace.AddressModel
import com.modul.marketplace.model.marketplace.LocationModel
import com.modul.marketplace.model.orderonline.DmDeliveryInfo
import com.modul.marketplace.model.orderonline.DmLocation
import com.modul.marketplace.restful.WSRestFull
import com.modul.marketplace.util.ToastUtil
import com.modul.marketplace.util.Utilities
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.include_header2.*
import java.util.*

class AddAddressOrderNvlActivity : BaseActivity() {

    private var cityCode = ""
    private var pricintCode = ""
    private var districtCode = ""
    private var lat = 0.0
    private var lng = 0.0
    private var localModel : LocationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        initExtra()
        initData()
        initClick()
    }

    private fun initExtra() {
        localModel = intent?.extras?.let {
            if (it.containsKey(Constants.OBJECT)) {
                it.getSerializable(Constants.OBJECT) as LocationModel?
            } else {
                null
            }
        }


        localModel?.run{
            city_uid?.run{
                cityCode = this
            }
            district_uid?.run{
                districtCode = this
            }
            precinct_uid?.run{
                pricintCode = this
            }
            mAddress.setText(StringExt.isTextEmpty(location_name))
            mCity.setText(StringExt.isTextEmpty(city?.city_name))
            mDistrict.setText(StringExt.isTextEmpty(district?.district_name))
            mPhuongXa.setText(StringExt.isTextEmpty(precinct?.precinct_name))
            mlbTitle.text = getString(R.string.edit_address)
        }?:run{
            mlbTitle.text = getString(R.string.add_address)
        }
    }

    private fun initClick() {
        mIconLeft.setOnClickListener { onBackPressed() }
        mCancel.setOnClickListener { onBackPressed() }
        mAddress.setOnClickListener { address() }
        mCity.setOnClickListener { choiceCity() }
        mDistrict.setOnClickListener { choiceDistric() }
        mPhuongXa.setOnClickListener { choicePrecinct() }
        mApply.setOnClickListener { save() }
    }

    private fun choiceCity() {
        showProgressHub(this)
        WSRestFull(this).apiSCMCity({ (data) -> areaDone(data) }) { error: VolleyError ->
            areaDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun areaDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mCity, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.city_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mCity.setText(cityName)
                cityCode = data[item.itemId.minus(1)].id.toString()
                false
            }
            popupMenu.show()
        }
    }

    private fun choicePrecinct() {
        if (districtCode == "") {
            showToast(getString(R.string.district_valid))
            return
        }
        showProgressHub(this)
        var addressModel = AddressModel()
        addressModel.city_id = cityCode
        addressModel.district_id = districtCode
        WSRestFull(this).apiSCMPrecincts(addressModel, { (data) -> precinctDone(data) }) { error: VolleyError ->
            precinctDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun precinctDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mPhuongXa, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.precinct_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mPhuongXa.setText(cityName)
                pricintCode = data[item.itemId.minus(1)].id.toString()
                false
            }
            popupMenu.show()
        }
    }

    private fun choiceDistric() {
        if (cityCode == "") {
            showToast(getString(R.string.city_valid))
            return
        }
        showProgressHub(this)
        var addressModel = AddressModel()
        addressModel.city_id = cityCode
        WSRestFull(this).apiSCMDistricts(addressModel, { (data) -> districDone(data) }) { error: VolleyError ->
            districDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun districDone(data: ArrayList<AddressModel>?) {
        dismissProgressHub()
        data?.run {
            val popupMenu = PopupMenu(this@AddAddressOrderNvlActivity, mDistrict, Gravity.RIGHT)

            forEachIndexed { index, team ->
                popupMenu.menu.add(0, index.plus(1), index.plus(1), team.district_name)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val cityName = item.title.toString()
                mDistrict.setText(cityName)
                districtCode = data[item.itemId.minus(1)].id.toString()
                false
            }
            popupMenu.show()
        }
    }

    private fun save() {
        var adressdl = mAddress.text.toString()

        if (TextUtils.isEmpty(adressdl)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
        if (TextUtils.isEmpty(cityCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
        if (TextUtils.isEmpty(districtCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }
        if (TextUtils.isEmpty(pricintCode)) {
            ToastUtil.makeText(this, getString(R.string.mess_delivery_info))
            return
        }

        val dmDeliveryInfo = DmDeliveryInfo()
        dmDeliveryInfo.address = adressdl
        dmDeliveryInfo.city = cityCode
        dmDeliveryInfo.district = districtCode
        dmDeliveryInfo.lat = lat
        dmDeliveryInfo.lng = lng

        mCartBussiness.getOrder().dmDeliveryInfo = dmDeliveryInfo

        var locateModel = LocationModel()
        locateModel.selected = null
        locateModel.location_name = adressdl
        locateModel.company_id = mCartBussiness.companyId
        locateModel.user_id = mCartBussiness.userId
        locateModel.precinct_uid = pricintCode
        locateModel.district_uid = districtCode
        locateModel.city_uid = cityCode
        locateModel.country_uid = "88a736a3-747c-4ed8-8426-cfe995691938"

        showProgressHub(this)
        localModel?.run{
            locateModel.id = id
            apiEdit(locateModel)
        }?:run{
            apiCreate(locateModel)
        }
    }

    private fun apiCreate(locateModel: LocationModel){
        WSRestFull(this).apiSCMLocationCreate(locateModel.toJson(), { (data) -> createDone(data) }) { error: VolleyError ->
            createDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun apiEdit(locateModel: LocationModel){
        WSRestFull(this).apiSCMLocationEdit(locateModel.toJson(), { (data) -> createDone(data) }) { error: VolleyError ->
            createDone(null)
            error.printStackTrace()
            ToastUtil.makeText(this, getString(R.string.error_network2))
        }
    }

    private fun createDone(data: LocationModel?) {
        dismissProgressHub()
        data?.run {
            val i = Intent()
            setResult(RESULT_OK, i)
            onBackPressed()
        }
    }

    private fun address() {
        GPS()
    }

    private fun GPS() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                Utilities.showMessageOKCancel(this, getString(R.string.mess_acces_permission_location)
                ) { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            } else {
                ActivityCompat.requestPermissions(this, Utilities.PERMISSIONS_LOCATION, 1)
            }
        } else {
            val i = Intent(this, MapActivity::class.java)
            startActivityForResult(i, InformationFragment.REQUEST_CODE_PICK_LOCATION)
        }
    }

    private fun initData() {
        showStatusBar(color = R.color.white, statusColor = true)
        include2.background.setTint(ContextCompat.getColor(this, R.color.white))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InformationFragment.REQUEST_CODE_PICK_LOCATION) {
            if (resultCode == RESULT_OK) {
                val dmLocation = data!!.getSerializableExtra(Constants.KEY_DATA) as DmLocation
                val ad = dmLocation.address
                lat = dmLocation.latitude
                lng = dmLocation.longitude
                mAddress.setText(ad)
            }
        }
    }
}