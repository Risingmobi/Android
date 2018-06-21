package com.alex.tur.client.ui.profile

import android.arch.lifecycle.*
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.profile.ProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Customer
import com.alex.tur.model.MyAddress
import com.alex.tur.scheduler.SchedulerProvider
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel
@Inject constructor(
        private val profileDataManager: ProfileDataManager,
        private val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    private val profileTrigger = SingleLiveEvent<Boolean>()
    val profileResult: LiveData<Result<Customer>> = Transformations.switchMap(profileTrigger, {
        profileDataManager.getProfile(it)
    })

    val editNameAction = SingleLiveEvent<String?>()
    val editEmailAction = SingleLiveEvent<String?>()
    val editPasswordAction = SingleLiveEvent<String?>()
    val editAddressAction = SingleLiveEvent<MyAddress?>()
    val editPhoneAction = SingleLiveEvent<String?>()
    val editCardAction = SingleLiveEvent<Unit>()

    val changeAvatarError = SingleLiveEvent<String?>()

    val logoutAction = SingleLiveEvent<Unit>()

    val pickImageClickHandler = SingleLiveEvent<Unit>()
    val takePhotoClickHandler = SingleLiveEvent<Unit>()

    fun requestData(isForce: Boolean = false) {
        profileTrigger.value = isForce
    }

    fun onEditNameClicked() {
        editNameAction.value = profileResult.value?.data?.name
    }

    fun onEditEmailClicked() {
        editEmailAction.value = profileResult.value?.data?.email
    }

    fun onEditPhoneClicked() {
        editPhoneAction.value = profileResult.value?.data?.phone
    }

    fun onEditAddressClicked() {
        val lat = profileResult.value?.data?.lat
        val lng = profileResult.value?.data?.lng
        if (lat != null && lng != null) {
            editAddressAction.value = MyAddress.home(LatLng(lat, lng), profileResult.value?.data?.addressString)
        } else {
            editAddressAction.call()
        }
    }

    fun onEditPasswordClicked() {
        editPasswordAction.value = profileResult.value?.data?.password
    }

    fun onPickImageClicked() {
        pickImageClickHandler.call()
    }

    fun onTakePhotoClicked() {
        takePhotoClickHandler.call()
    }

    fun onEditPayCardClicked() {
        editCardAction.call()
    }

    fun onLogoutClicked() {
        profileDataManager.logout()
                .subscribe({
                    logoutAction.call()
                }, {
                    Timber.e(it, "logout")
                })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {

    }

    override fun onCleared() {
        Timber.d("onCleared")
    }

    fun uploadAvatar(avatar: String?) {
        avatar?.let {
            profileDataManager.changeAvatar(avatar)
                    .subscribe({
                    }, {
                        changeAvatarError.value = "Upload error"
                        Timber.e(it, "upload avatar")
                    })
        }
    }


}