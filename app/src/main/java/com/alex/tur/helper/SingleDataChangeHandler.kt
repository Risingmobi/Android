package com.alex.tur.helper

import android.arch.lifecycle.MutableLiveData
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber
//TODO implement coroutines
abstract class SingleDataChangeHandler<RequestType, ResultType>(private val schedulerProvider: SchedulerProvider, private val tag: String) {

    val result = MutableLiveData<Result<RequestType>>()

    var disposable: Disposable? = null

    private var isExecuting = false

    private var changingData: RequestType? = null

    fun execute(newData: RequestType, currentData: RequestType) {
        if (isExecuting) {
            return
        }
        changingData = newData
        isExecuting = true
        result.value = Result.loading(changingData!!)
        disposable = performChange(newData)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    isExecuting = false
                    changingData = saveAndGetResult(it)
                    changingData?.let {
                        result.value = Result.successFromRemote(it)
                    }
                }, {
                    isExecuting = false
                    result.value = Result.errorFromRemote(it.localizedMessage, currentData)
                    Timber.e(it)
                })
    }

    fun update(data: RequestType?): MutableLiveData<Result<RequestType>> {
        Timber.d("update %s: %s", tag, isExecuting)
        if (!isExecuting) {
            data?.let {
                result.value = Result.successFromRemote(it)
            }
        }
        return result
    }

    protected abstract fun performChange(type: RequestType): Single<ResultType>
    protected abstract fun saveAndGetResult(data: ResultType): RequestType?
}