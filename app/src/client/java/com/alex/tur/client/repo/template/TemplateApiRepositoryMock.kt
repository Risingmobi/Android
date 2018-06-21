package com.alex.tur.client.repo.template

import com.alex.tur.model.Template
import com.alex.tur.model.api.ResponseTemplate
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

class TemplateApiRepositoryMock : TemplateApiRepository {
    override fun uploadTemplate(header: String, picturePart: MultipartBody.Part?, latPart: RequestBody, lngPart: RequestBody, timePart: RequestBody, dayOfWeekPart: RequestBody, commentPart: RequestBody?, serviceIdPart: RequestBody): Single<Unit> {
        return Single.fromCallable { Unit }
    }


    override fun getTemplates(header: String): Single<MutableList<ResponseTemplate>> {
        return Single.fromCallable {
            mutableListOf<ResponseTemplate>().apply {
//                add(Template(0, 0, 35.3455345, 42.3546433, "12:45", DayOfWeek.MONDAY, OrderDescription(), Template.ServiceDescription().apply { id = 0 }))
//                add(Template(1, 1, 35.4435345, 42.3563545, "13:45", DayOfWeek.TUESDAY, OrderDescription(), Template.ServiceDescription().apply { id = 1 }))
//                add(Template(2, 2, 3, 35.3454633, 42.3256455, "14:45", DayOfWeek.WEDNESDAY))
//                add(Template(3, 3, 4, 35.2235355, 42.3543525, "15:45", DayOfWeek.THURSDAY))
//                add(Template(4, 4, 5, 35.2643545, 42.3453523, "16:45", DayOfWeek.FRIDAY))
//                add(Template(5, 5, 6, 35.3454355, 42.3345535, "17:45", DayOfWeek.SATURDAY))
//                add(Template(6, 6, 7, 35.3455535, 42.3535456, "18:45", DayOfWeek.SUNDAY))
            }
        }.delay(5, TimeUnit.SECONDS)
    }
}