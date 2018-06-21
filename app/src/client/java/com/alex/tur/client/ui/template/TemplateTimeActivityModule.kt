package com.alex.tur.client.ui.template

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.client.ui.template.TemplateTimeActivity.Companion.EXTRA_TEMPLATE
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.model.Template
import dagger.Module
import dagger.Provides

@Module
abstract class TemplateTimeActivityModule {

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        internal fun template(activity: TemplateTimeActivity): LiveData<Template> {
            return MutableLiveData<Template>().apply {
                value = activity.intent.getSerializableExtra(EXTRA_TEMPLATE) as Template?
            }
        }
    }
}