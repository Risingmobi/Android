package com.alex.tur.driver.ui.profile.edit

import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.ui.profile.edit.EditActivity.Companion.EXTRA_EDIT_PARAM
import com.alex.tur.ui.profile.edit.EditActivity.Companion.EXTRA_EDIT_TYPE
import com.alex.tur.ui.profile.edit.QualifierEditParam
import com.alex.tur.ui.profile.edit.QualifierEditType
import dagger.Module
import dagger.Provides

@Module
abstract class DriverEditActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierEditType
        internal fun editType(activity: DriverEditActivity): String? {
            return activity.intent.getStringExtra(EXTRA_EDIT_TYPE)
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierEditParam
        internal fun editParam(activity: DriverEditActivity): String? {
            return activity.intent.getStringExtra(EXTRA_EDIT_PARAM)
        }
    }
}