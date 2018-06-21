package com.alex.tur.driver.ui.profile.edit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class DriverEditViewModel @Inject constructor(
        private val profileDataManager: DriverProfileDataManager
): ViewModel() {

    val action = MutableLiveData<Result<Unit>>()

    private var disposable: Disposable? = null

    fun onEditPasswordClicked(password: String) {
        if (disposable != null && !disposable!!.isDisposed) {
            return
        }
        action.value = Result.loading()
        disposable = profileDataManager.changePassword(password)
                .subscribe({
                    dispose()
                    action.value = Result.successFromRemote(Unit)
                },{
                    dispose()
                    action.value = Result.errorFromRemote(it.localizedMessage)
                })
    }

    private fun dispose() {
        disposable?.dispose()
        disposable = null
    }
}