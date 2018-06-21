package com.alex.tur.client.ui.companies

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.services.ServicesDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Company
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(
        private val servicesDataManager: ServicesDataManager
): BaseViewModel() {

    private var query: String? = null

    private val companiesRequest = SingleLiveEvent<CompanyListRequest>()
    val companies: LiveData<Result<MutableList<Company>>> = Transformations.switchMap(companiesRequest, {
        Transformations.map(servicesDataManager.getCompanies(it), {
            if (query.isNullOrBlank() || it.data == null) {
                return@map it
            } else {
                val filtered = mutableListOf<Company>()
                for (company in it.data!!) {
                    if (query?.toLowerCase()?.let { it1 -> company.naming?.toLowerCase()?.contains(it1) } == true) {
                        filtered.add(company)
                    }
                }
                return@map Result.successFromRemote(filtered)
            }
        })
    })

    fun requestCompanies(naming: String?, isForce: Boolean = false) {
        naming?.also {
            companiesRequest.setValue(CompanyListRequest(it, isForce))
        }
    }

    fun onQueryTextChange(naming: String?, query: String) {
        this.query = query
        naming?.also {
            companiesRequest.setValue(CompanyListRequest(it, false))
        }
    }

    fun finishing() {
        servicesDataManager.clearCompanies()
    }

    class CompanyListRequest(val naming: String, val isForce: Boolean)
}