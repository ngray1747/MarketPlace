package com.modul.marketplace.app

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.FirebaseApp
import com.modul.marketplace.bussiness.CartBussiness
import com.modul.marketplace.bussiness.LocationBussiness
import com.modul.marketplace.model.orderonline.DmBrand
import com.modul.marketplace.model.orderonline.DmStore
import com.modul.marketplace.restful.AbsRestful
import com.modul.marketplace.restful.NukeSSLCerts
import com.modul.marketplace.restful.OkHttpStack
import com.modul.marketplace.util.FormatNumberUtil
import ly.count.android.sdk.Countly
import ly.count.android.sdk.CountlyConfig
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import vn.momo.momo_partner.AppMoMoLib

import vn.zalopay.sdk.ZaloPaySDK

class ApplicationMarketPlace : Application() {
    private var mRequestQueue: RequestQueue? = null
    var locationBussiness: LocationBussiness? = null
    var cartBussiness: CartBussiness? = null

    private val applicationMarketPlace: ApplicationMarketPlace? = null

    fun initContext(c: Context){
        context = c
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseApp.getApps(this)
        Log.d("APP", "ON APP")
        initBussiness()
        Log.i("APP", "ON APP Width/height ")
    }



    fun loadData(
            companyId: String? = null,
            brandId: String? = null,
            userId: String? = null,
            appType: String? = null,
            listBrand: ArrayList<DmBrand>? = null,
            listStore: ArrayList<DmStore>? = null
    ) {
        companyId?.run {
            cartBussiness?.companyId = this
        }
        brandId?.run {
            cartBussiness?.brandId = this
        }
        userId?.run {
            cartBussiness?.userId = this
        }
        appType?.run {
            cartBussiness?.appType = this
        }
        listBrand?.run {
            cartBussiness?.listBrand = ArrayList()
            cartBussiness?.listBrand?.addAll(this)
        }
        listStore?.run {
            cartBussiness?.listStore = ArrayList()
            cartBussiness?.listStore?.addAll(this)
        }
    }

    fun initBussiness() {
        instance = this
        locationBussiness = LocationBussiness(context)
        cartBussiness = CartBussiness()
        FormatNumberUtil.initInStance()
        Countly.applicationOnCreate()
        val config = CountlyConfig(
                context,
                "12ee977476a2b38f5a6423f336c8e9f1e2ec7529",
                "https://analytic.ipos.vn"
        )
        config.setLoggingEnabled(true)
        config.enableCrashReporting()
        config.setApplication(this)
        Countly.sharedInstance().init(config)
        ZaloPaySDK.getInstance().initWithAppId(684)
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION)

//        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this)
//            //            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
//            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION)
//        } else {
//            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION)
//            val config1 = SharedPref(this)
//            val sethoe = config1.getString(Constants.KEY_ANDROID_STETHO, "1")
//            if ("1" == sethoe) {
//                Stetho.initializeWithDefaults(this)
//            }
//        }
    }

    fun changeDeviceIdCounly(deviceId: String?) {
        Countly.sharedInstance().changeDeviceIdWithMerge(deviceId)
    }//mRequestQueue = Volley.newRequestQueue(getApplicationContext());

    // lazy initialize the request queue, the queue instance will be
    // created when it is accessed for the first time

    fun getRequestQueue(): RequestQueue? {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            //mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            val builderOk = OkHttpClient.Builder()
                    .addNetworkInterceptor(StethoInterceptor())
            mRequestQueue = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build()
                val nothttps = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                        .build()
                val list = java.util.ArrayList<ConnectionSpec>()
                list.add(spec)
                list.add(nothttps)
                builderOk.connectionSpecs(list)
                Volley.newRequestQueue(context, OkHttpStack(builderOk.build()))
            } else {
                NukeSSLCerts.nuke()
                Volley.newRequestQueue(context)
            }
            VolleyLog.DEBUG = Constants.IS_LOG
        }
        return mRequestQueue
    }


    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        req.retryPolicy = AbsRestful.reTryPolicy()
        Log.i(
                "App.addToRequestQueue() tag $tag",
                "url restful " + req.url
        )
        getRequestQueue()?.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        // set the default tag if tag is empty
        req.tag = TAG
        req.retryPolicy = AbsRestful.reTryPolicy()
        Log.i("App.addToRequestQueue()", "url restful " + req.url)
        getRequestQueue()?.add(req)
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    fun cancelPendingRequests(tag: Any) {
        Log.i("Cancel Request", "destroy request $tag")
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    companion object {
        lateinit var instance: ApplicationMarketPlace
        lateinit var context: Context
    }
}