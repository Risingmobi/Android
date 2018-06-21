package com.alex.tur.client.ui.template

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.client.datamanager.template.TemplateDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.DayOfWeek
import com.alex.tur.model.Template
import timber.log.Timber
import javax.inject.Inject

class TemplateViewModel @Inject constructor(
        val template: LiveData<Template>,
        val templateDataManager: TemplateDataManager
): BaseViewModel() {

    val selectedDayOfWeek = MutableLiveData<DayOfWeek>()
    private val createTemplateTrigger = SingleLiveEvent<Template>()
    val createTemplate: LiveData<Result<Unit>> = Transformations.switchMap(createTemplateTrigger, {
        templateDataManager.createTemplate(it)
    })

    fun createTemplate(hour: Int, minute: Int) {
        template.value?.time = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
        template.value?.dayOfWeek = selectedDayOfWeek.value
        createTemplateTrigger.value = template.value
        Timber.d("createTemplate ${template.value}")
    }

    fun setSelectedDayOfWeek(dayOfWeek: DayOfWeek) {
        selectedDayOfWeek.value = dayOfWeek
    }
}