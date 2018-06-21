package com.alex.tur.client.datamanager.services

import android.arch.lifecycle.LiveData
import com.alex.tur.client.repo.services.ServicesApiRepository
import com.alex.tur.client.ui.companies.CompaniesViewModel
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.model.Company
import com.alex.tur.model.Service
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class ServicesDataManagerImpl @Inject constructor(
        private val accountManager: MyAccountManager,
        private val servicesApiRepository: ServicesApiRepository,
        private val schedulerProvider: SchedulerProvider
): ServicesDataManager {

    private val servicesWithCompaniesLoader = object: SingleDataLoadingHandler<MutableList<Service>>(schedulerProvider, "SERVICES") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Service>>? {
            return null
        }
        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Service>> {
            return servicesApiRepository.getServicesWithCompanies(accountManager.getAuthHeader())
                    .map {
                        val services = mutableListOf<Service>()
                        for (item in it) {
                            services.add(Service().apply {
                                naming = item.naming
                                picture = item.picture
                            })
                        }
                        services
                    }
        }
        override fun saveFromNet(data: MutableList<Service>) {
            //TODO
        }
    }

    private val companiesLoader = object: SingleDataLoadingHandler<MutableList<Company>>(schedulerProvider, "SERVICES") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<Company>>? {
            return null
        }
        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<Company>> {
            return servicesApiRepository.getCompaniesByServiceName(accountManager.getAuthHeader(), params!!["naming"]!!)
                    .map {response ->
                        val list = mutableListOf<Company>()
                        response.companies?.also {
                            for (item in it) {
                                val company = Company()
                                company.id = item.id
                                company.naming = item.naming
                                company.cost = item.cost
                                company.picture = item.picture
                                company.service_id = item.service_id
                                list.add(company)
                            }
                        }
                        list
                    }
                    .observeOn(schedulerProvider.ui())
        }
        override fun saveFromNet(data: MutableList<Company>) {
            //TODO
        }
    }

    override fun getServices(isForce: Boolean): LiveData<Result<MutableList<Service>>> {
        return servicesWithCompaniesLoader.execute(isForce)
    }

    override fun getCompanies(request: CompaniesViewModel.CompanyListRequest): LiveData<Result<MutableList<Company>>> {
        val params = mutableMapOf<String, String>().apply {
            put("naming", request.naming)
        }
        return companiesLoader.execute(request.isForce, null, params)
    }

    override fun clearServices() {
        servicesWithCompaniesLoader.resultLiveData.value = null
    }

    override fun clearCompanies() {
        companiesLoader.resultLiveData.value = null
    }
}