package com.alex.tur.client.datamanager.template

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.Template
import com.alex.tur.model.api.ResponseTemplate

interface TemplateDataManager {
    fun getTemplates(isForce: Boolean): LiveData<Result<MutableList<ResponseTemplate>>>
    fun createTemplate(template: Template): LiveData<Result<Unit>>
}