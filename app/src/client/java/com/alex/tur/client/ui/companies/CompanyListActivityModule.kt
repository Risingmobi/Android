package com.alex.tur.client.ui.companies

import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.model.Service
import dagger.Module
import dagger.Provides

@Module
abstract class CompanyListActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        fun provideService(activity: CompanyListActivity): Service {
            return activity.intent.getSerializableExtra(CompanyListActivity.EXTRA_SERVICE) as Service
        }
    }
}