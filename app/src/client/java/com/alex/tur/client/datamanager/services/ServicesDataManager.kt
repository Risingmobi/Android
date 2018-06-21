package com.alex.tur.client.datamanager.services

import android.arch.lifecycle.LiveData
import com.alex.tur.client.ui.companies.CompaniesViewModel
import com.alex.tur.helper.Result
import com.alex.tur.model.Company
import com.alex.tur.model.Service

interface ServicesDataManager {
    fun getServices(isForce: Boolean): LiveData<Result<MutableList<Service>>>
    fun getCompanies(request: CompaniesViewModel.CompanyListRequest): LiveData<Result<MutableList<Company>>>
    fun clearServices()
    fun clearCompanies()
}