package com.alex.tur.driver.ui.profile

import android.arch.lifecycle.*
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.driver.ui.profile.edit_car.EditCarActivity
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Driver
import com.alex.tur.model.DriverStatus
import com.alex.tur.scheduler.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel
@Inject constructor(
        private val profileDataManager: DriverProfileDataManager,
        private val schedulerProvider: SchedulerProvider
): ViewModel(), LifecycleObserver {

    init {
        Timber.tag("datamanager_test").d("ProfileViewModel profileDataManager %s", profileDataManager.hashCode())
    }

    val logoutAction = SingleLiveEvent<Unit>()

    val editPasswordAction = SingleLiveEvent<String?>()
    val editCarAction = SingleLiveEvent<EditCarActivity.CarInfo>()

    private val profileRequest = SingleLiveEvent<Boolean>()
    val profile: LiveData<Result<Driver>> = Transformations.switchMap(profileRequest, {
        profileDataManager.getProfile(it)
    })

    val status = profileDataManager.getStatus()

    fun requestData(isForce: Boolean = false) {
        profileRequest.value = isForce
    }

    fun onEditPasswordClicked() {
        editPasswordAction.value = profile.value?.data?.password
    }

    fun onEditCarClicked() {
        editCarAction.value = EditCarActivity.CarInfo(profile.value?.data?.vehicleModel, profile.value?.data?.vehicleNumber)
    }

    fun setAvailable(isAvailable: Boolean) {
        val newStatus = if (isAvailable) {
            DriverStatus.ACTIVE
        } else {
            DriverStatus.INACTIVE
        }
        status.value?.data?.let {
            profileDataManager.changeStatus(newStatus, it)
        }
    }

    fun onLogOutClicked() {
        profileDataManager.logOut()
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    logoutAction.call()
                }, {
                    Timber.e(it, "logOut")
                })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.d("onDestroy")
    }
}