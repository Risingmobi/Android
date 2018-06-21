package com.alex.tur.driver.ui.servicelist

import android.arch.lifecycle.*
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Service
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class DriverServicesViewModel @Inject constructor(
        private val profileDataManager: DriverProfileDataManager,
        private val schedulerProvider: SchedulerProvider
): ViewModel(), LifecycleObserver {

    private var shouldReset = true

    val notifyDataChangedAction = SingleLiveEvent<Unit>()
    private val servicesRequest = SingleLiveEvent<Boolean>()
    val services : LiveData<Result<MutableList<Service>>> = Transformations.switchMap(servicesRequest, {
        profileDataManager.getServices(it)
    })


    val saveServices = SingleLiveEvent<Result<Unit>>()

    fun requestServices(isForce: Boolean = false) {
        servicesRequest.value = isForce
    }

    private var updateServicesDisposable: Disposable? = null

    fun save() {
        if (updateServicesDisposable != null && !updateServicesDisposable!!.isDisposed) {
            return
        }
        val checkedServices = mutableListOf<Service>()
        services.value?.data?.let {
            for (service in it) {
                if (service.isChecked) {
                    checkedServices.add(service)
                }
            }
            saveServices.value = Result.loading()
            updateServicesDisposable = profileDataManager.changeServices(checkedServices)
                    .subscribe({
                        shouldReset = false
                        disposeServicesDisposable()
                        saveServices.value = Result.successFromRemote(Unit)
                    }, {
                        disposeServicesDisposable()
                        Timber.e(it)
                        saveServices.value = Result.errorFromRemote(it.localizedMessage)
                    })
        }
    }

    fun reset() {
        services.value?.data?.let {
            for (service in it) {
                service.isChecked = service.isMine
            }
            notifyDataChangedAction.call()
        }
    }

    private fun disposeServicesDisposable() {
        updateServicesDisposable?.dispose()
        updateServicesDisposable = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposeServicesDisposable()
    }

    fun finishing() {
        if (shouldReset) {
            reset()
        }
    }
}