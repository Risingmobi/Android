package com.alex.tur.driver.ui.profile.edit_car

import com.alex.tur.di.scope.ScopeActivity
import dagger.Module
import dagger.Provides

@Module
class EditCarActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        internal fun carInfo(activity: EditCarActivity): EditCarActivity.CarInfo {
            return activity.intent.getSerializableExtra(EditCarActivity.EXTRA_CAR) as EditCarActivity.CarInfo
        }
    }
}