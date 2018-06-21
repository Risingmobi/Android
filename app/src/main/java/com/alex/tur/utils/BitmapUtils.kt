package com.alex.tur.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.widget.ImageView
import timber.log.Timber

object BitmapUtils {

    fun getBitmapFromResource(context: Context, @DrawableRes resId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, resId)
        return drawable?.let {
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            canvas.setBitmap(bitmap)
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
            bitmap
        }
    }

    fun getTintBitmapFromResource(context: Context, @DrawableRes resId: Int, @ColorRes color: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId) as Drawable
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    fun setColorFilter(context: Context?, imageView: ImageView, @ColorRes colorRes: Int) {
        context?:return
        imageView.setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC_ATOP)
    }

    fun setTint(context: Context?, imageView: ImageView, @ColorRes colorRes: Int) {
        context?:return
        imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    }

    fun clearColorFilter(context: Context?, imageView: ImageView) {
        context?:return
        imageView.clearColorFilter()
    }
}
