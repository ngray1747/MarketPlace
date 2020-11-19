package com.modul.marketplace.model.marketplace

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AhamoveSearchData(
    @SerializedName("features") var features: ArrayList<AhamoveSearchFeatures>? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun getFeaturesByName(name: String): AhamoveSearchFeatures? {
        return features?.filter {
            var isName = false
            if (!TextUtils.isEmpty(name)) {
                isName = it.properties?.name.toString() == name
            } else {
                isName = true
            }

            isName
        }?.first()
    }
}


data class AhamoveSearchFeatures(
    @SerializedName("geometry") var geometry: AhamoveSearchGeometry? = null,
    @SerializedName("properties") var properties: AhamoveSearchProperties? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}


data class AhamoveSearchGeometry(
    @SerializedName("coordinates") var coordinates: ArrayList<Double>? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class AhamoveSearchProperties(
    @SerializedName("id") var id: Any? = null,
    @SerializedName("region") var region: Any? = null,
    @SerializedName("county") var county: Any? = null,
    @SerializedName("street") var street: Any? = null,
    @SerializedName("locality") var locality: Any? = null,
    @SerializedName("name") var name: Any? = null
) : Serializable {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
