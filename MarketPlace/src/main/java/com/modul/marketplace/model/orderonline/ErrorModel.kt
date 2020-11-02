package com.modul.marketplace.model.orderonline

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Error(
        @SerializedName("message") var message: Any? = null,
        @SerializedName("code") var code: Int? = 0,
        @SerializedName("TrackId") var TrackId: Any? = null
) : Serializable
