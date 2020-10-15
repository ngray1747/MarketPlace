package com.modul.marketplace.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.modul.marketplace.R
import com.modul.marketplace.app.ApplicationMarketPlace
import de.hdodenhof.circleimageview.CircleImageView

fun initAvatar(view: CircleImageView, path: Any?, error: Drawable? = null) {
    Glide.with(ApplicationMarketPlace.instance).load(path).apply(RequestOptions().placeholder(error).override(100, 100)).into(view)
}

fun initAvatarCompany(c: Context, view: ImageView, path: Any?, error: Drawable? = null) {
    Glide.with(c).load(path).apply(
            RequestOptions().placeholder(error)
    ).into(view)
}

fun initIcon(c: Context,view: ImageView, path: Any?) {
    Glide.with(c).load(path).apply(
            RequestOptions().placeholder(R.drawable.icon_default).override(100, 100)
    ).into(view)
}

fun initIconNoDefault(c: Context,view: ImageView, path: Any?) {
    Glide.with(c).load(path).into(view)
}