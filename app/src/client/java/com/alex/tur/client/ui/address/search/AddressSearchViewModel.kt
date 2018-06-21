package com.alex.tur.client.ui.address.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.profile.ProfileDataManager
import com.alex.tur.di.qualifier.local.QualifierAddressCurrent
import com.alex.tur.di.qualifier.local.QualifierAddressHome
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.MyAddress
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class AddressSearchViewModel @Inject constructor(
        private val profileDataManager: ProfileDataManager,
        @QualifierAddressCurrent
        val addressCurrent: MutableLiveData<MyAddress?>,
        @QualifierAddressHome
        val addressHome: MutableLiveData<MyAddress?>
): BaseViewModel() {

    val addressAction = SingleLiveEvent<Result<Unit>>()
    private var addressDisposable: Disposable? = null

    fun changeAddress(address: MyAddress) {
        if (addressDisposable != null && !addressDisposable!!.isDisposed) {
            return
        }
        addressAction.value = Result.loading()
        addressDisposable = profileDataManager.changeAddress(address.latLng)
                .subscribe({
                    dispose()
                    addressAction.value = Result.successFromRemote(Unit)
                }, {
                    dispose()
                    addressAction.value = Result.errorFromRemote(it.localizedMessage)
                    Timber.e(it, "changeAddress error")
                })
    }

    private fun dispose() {
        addressDisposable?.dispose()
        addressDisposable = null
    }
}