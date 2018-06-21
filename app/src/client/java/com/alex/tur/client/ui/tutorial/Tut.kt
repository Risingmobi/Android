package com.alex.tur.client.ui.tutorial

import android.support.annotation.DrawableRes
import java.io.Serializable

data class Tut(
        @DrawableRes val image: Int,
        val title: String,
        val desc: String
): Serializable