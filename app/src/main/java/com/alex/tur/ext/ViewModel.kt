package com.alex.tur.ext

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.alex.tur.base.BaseViewModel
import com.alex.tur.di.module.ui.ViewModelFactory
import kotlin.reflect.KClass

fun <V: BaseViewModel> Fragment.getViewModel(modelClass: KClass<V>, factory: ViewModelFactory<V>): V {
    val viewModel = ViewModelProviders.of(this, factory).get(modelClass.java)
    lifecycle.addObserver(viewModel)
    return viewModel
}

fun <V: BaseViewModel> FragmentActivity.getViewModel(modelClass: KClass<V>, factory: ViewModelFactory<V>): V {
    val viewModel = ViewModelProviders.of(this, factory).get(modelClass.java)
    lifecycle.addObserver(viewModel)
    return viewModel
}