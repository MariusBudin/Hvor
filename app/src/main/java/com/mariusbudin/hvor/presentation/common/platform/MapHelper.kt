package com.mariusbudin.hvor.presentation.common.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun Context.getBitmapDescriptor(@DrawableRes id: Int): BitmapDescriptor? {
    val vectorDrawable = AppCompatResources.getDrawable(this, id) as VectorDrawable
    val h = vectorDrawable.intrinsicHeight
    val w = vectorDrawable.intrinsicWidth
    vectorDrawable.setBounds(0, 0, w, h)
    val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}