package com.alex.tur.di.module.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Lazy
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

class ViewModelFactory<VM: ViewModel>
@Inject constructor(
        private val viewModel: Lazy<VM>
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel.get() as T
    }
}