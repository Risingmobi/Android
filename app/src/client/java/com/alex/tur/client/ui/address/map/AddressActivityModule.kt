package com.alex.tur.client.ui.address.map

import android.arch.lifecycle.MutableLiveData
import com.alex.tur.client.ui.address.map.AddressMapActivity.Companion.EXTRA_MY_ADDRESS
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.model.MyAddress
import com.alex.tur.ui.profile.edit.QualifierAddressMapAction
import com.google.android.gms.maps.model.LatLng
import dagger.Module
import dagger.Provides

@Module
abstract class AddressActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        internal fun latLng(activity: AddressMapActivity): MutableLiveData<MyAddress?> {
            return MutableLiveData<MyAddress?>().apply {
                value = activity.intent.getParcelableExtra(EXTRA_MY_ADDRESS)
            }
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierAddressMapAction
        internal fun action(activity: AddressMapActivity): String? {
            return activity.intent.action
        }
    }
}