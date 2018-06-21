package com.alex.tur.client.datamanager.template

import android.arch.lifecycle.LiveData
import com.alex.tur.client.repo.template.TemplateApiRepository
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.data.db.MyDatabase
import com.alex.tur.data.pref.PrefManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.model.Template
import com.alex.tur.model.api.ResponseTemplate
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class TemplateDataManagerImpl @Inject constructor(
        private val templateApiRepository: TemplateApiRepository,
        private val myAccountManager: MyAccountManager,
        private val dataBase: MyDatabase,
        private val schedulerProvider: SchedulerProvider,
        private val prefsManager: PrefManager
) : TemplateDataManager {

    private val templatesLoader = object : SingleDataLoadingHandler<MutableList<ResponseTemplate>>(schedulerProvider, "TEMPLATES") {
        override fun loadFromDb(params: Map<String, String>?): Single<MutableList<ResponseTemplate>>? {
            return Single.fromCallable { dataBase.customerDao().findAllTemplates() }
        }

        override fun loadFromNet(params: Map<String, String>?): Single<MutableList<ResponseTemplate>> {
            return templateApiRepository.getTemplates(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: MutableList<ResponseTemplate>) {
            Timber.d("saveFromNet ${data[0]}")
            dataBase.customerDao().removeAll()
            dataBase.customerDao().insertTemplates(data)
        }
    }

    private val templateUploader = object : SingleActionHandler<Template, Unit>(schedulerProvider, "TEMPLATE_UPLOAD") {

        override fun performAction(requestData: Template): Single<Unit> {
            var picturePart: MultipartBody.Part? = null
            requestData.requestDescription?.picture?.also { picture ->
                val file = File(requestData.requestDescription?.picture)
                Timber.d("picture size ${file.length()}")
                val pictureRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                picturePart = MultipartBody.Part.createFormData("request_descr_picture", file.name, pictureRequestBody)
            }

            val commentPart = requestData.requestDescription?.brieflyDescription?.let {
                RequestBody.create(MediaType.parse("text/plain"), it)
            }

            val latPart = RequestBody.create(MediaType.parse("text/plain"), requestData.lat.toString())
            val lngPart = RequestBody.create(MediaType.parse("text/plain"), requestData.lng.toString())
            val timePart = RequestBody.create(MediaType.parse("text/plain"), requestData.time)
            val dayOfWeekPart = RequestBody.create(MediaType.parse("text/plain"), requestData.dayOfWeek?.day)
            val serviceIdPart = RequestBody.create(MediaType.parse("text/plain"), requestData.serviceDescription?.id.toString())

            return templateApiRepository.uploadTemplate(
                    myAccountManager.getAuthHeader(),
                    picturePart,
                    latPart,
                    lngPart,
                    timePart,
                    dayOfWeekPart,
                    commentPart,
                    serviceIdPart)
        }
    }

    override fun getTemplates(isForce: Boolean): LiveData<Result<MutableList<ResponseTemplate>>> {
        return templatesLoader.execute(isForce)
    }

    override fun createTemplate(template: Template): LiveData<Result<Unit>> {
        return templateUploader.execute(template)
    }
}