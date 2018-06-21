package com.alex.tur.client.ui.address.search

import android.arch.lifecycle.MutableLiveData
import com.alex.tur.client.ui.address.search.AddressSearchActivity.Companion.EXTRA_CURRENT_ADDRESS
import com.alex.tur.client.ui.address.search.AddressSearchActivity.Companion.EXTRA_HOME_ADDRESS
import com.alex.tur.di.qualifier.local.QualifierAddressCurrent
import com.alex.tur.di.qualifier.local.QualifierAddressHome
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.model.MyAddress
import com.alex.tur.ui.profile.edit.QualifierAddressMapAction
import dagger.Module
import dagger.Provides

@Module
abstract class AddressSearchActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierAddressCurrent
        internal fun currentLatLng(activity: AddressSearchActivity): MutableLiveData<MyAddress?> {
            return MutableLiveData<MyAddress?>().apply {
                value = activity.intent.getParcelableExtra(EXTRA_CURRENT_ADDRESS)
            }
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierAddressHome
        internal fun homeLatLng(activity: AddressSearchActivity): MutableLiveData<MyAddress?> {
            return MutableLiveData<MyAddress?>().apply {
                value = activity.intent.getParcelableExtra(EXTRA_HOME_ADDRESS)
            }
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierAddressMapAction
        internal fun action(activity: AddressSearchActivity): String? {
            return activity.intent.action
        }
    }
}