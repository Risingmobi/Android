package com.alex.tur.utils

import android.os.Looper

object ThreadUtils {

    fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }
}