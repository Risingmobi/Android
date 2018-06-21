package com.alex.tur.client.ui.profile.edit

import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.ui.profile.edit.EditActivity.Companion.EXTRA_EDIT_PARAM
import com.alex.tur.ui.profile.edit.EditActivity.Companion.EXTRA_EDIT_TYPE
import com.alex.tur.ui.profile.edit.QualifierEditParam
import com.alex.tur.ui.profile.edit.QualifierEditType
import dagger.Module
import dagger.Provides

@Module
abstract class ClientEditActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierEditType
        internal fun editType(activity: ClientEditActivity): String? {
            return activity.intent.getStringExtra(EXTRA_EDIT_TYPE)
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        @QualifierEditParam
        internal fun editParam(activity: ClientEditActivity): String? {
            return activity.intent.getStringExtra(EXTRA_EDIT_PARAM)
        }
    }
}