package com.alex.tur.client.ui.profile.edit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alex.tur.client.datamanager.profile.ProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class ClientEditViewModel @Inject constructor(
        private val profileDataManager: ProfileDataManager,
        private val schedulerProvider: SchedulerProvider
): ViewModel() {

    val action = MutableLiveData<Result<Unit>>()

    private var disposable: Disposable? = null

    fun onEditNameClicked(name: String) {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }
        action.value = Result.loading()
        disposable = profileDataManager.changeName(name)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    dispose()
                    action.value = Result.successFromRemote(Unit)
                },{
                    dispose()
                    action.value = Result.errorFromRemote(it.localizedMessage)
                    Timber.e(it, "changeName")
                })
    }

    fun onEditEmailClicked(email: String) {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }
        action.value = Result.loading()
        disposable = profileDataManager.changeEmail(email)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    dispose()
                    action.value = Result.successFromRemote(Unit)
                },{
                    dispose()
                    action.value = Result.errorFromRemote(it.localizedMessage)
                    Timber.e(it, "changeEmail")
                })
    }

    fun onEditPasswordClicked(password: String) {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }
        action.value = Result.loading()
        disposable = profileDataManager.changePassword(password)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    dispose()
                    action.value = Result.successFromRemote(Unit)
                },{
                    dispose()
                    action.value = Result.errorFromRemote(it.localizedMessage)
                    Timber.e(it, "changePassword")
                })
    }

    fun onEditPhoneClicked(phone: String) {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }
        action.value = Result.loading()
        disposable = profileDataManager.changePhone(phone)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    dispose()
                    action.value = Result.successFromRemote(Unit)
                },{
                    dispose()
                    action.value = Result.errorFromRemote(it.localizedMessage)
                    Timber.e(it, "changePhone")
                })
    }

    private fun dispose() {
        disposable?.dispose()
        disposable = null
    }
}