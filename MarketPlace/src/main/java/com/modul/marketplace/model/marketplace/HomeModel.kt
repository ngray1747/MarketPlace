package com.modul.marketplace.model.marketplace

import java.io.Serializable

data class HomeModel(
        var image: Int,
        var name: Any? = null
) : Serializable

data class SettingModel(
        var name: Any? = null,
        var image: Int
) : Serializable