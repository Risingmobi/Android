package com.alex.tur.client.ui.services

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.services.ServicesDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Service
import javax.inject.Inject

class ServicesViewModel @Inject constructor(
        private val servicesDataManager: ServicesDataManager
): BaseViewModel() {

    private var query: String? = null

    private val servicesRequest = SingleLiveEvent<Boolean>()
    val services: LiveData<Result<MutableList<Service>>> = Transformations.switchMap(servicesRequest, {
        Transformations.map(servicesDataManager.getServices(it), {
            if (query.isNullOrBlank() || it.data == null) {
                return@map it
            } else {
                val filtered = mutableListOf<Service>()
                for (service in it.data!!) {
                    if (query?.toLowerCase()?.let { it1 -> service.naming?.toLowerCase()?.contains(it1) } == true) {
                        filtered.add(service)
                    }
                }
                return@map Result.successFromRemote(filtered)
            }
        })
    })

    fun requestServices(isForce: Boolean = false) {
        servicesRequest.setValue(isForce)
    }

    fun onQueryTextChange(text: String) {
        query = text
        servicesRequest.setValue(false)
    }

    fun finishing() {
        servicesDataManager.clearServices()
    }
}