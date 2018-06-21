package com.alex.tur.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber

//TODO implement coroutines
abstract class SingleActionHandler<RequestType, ResultType>(private val schedulerProvider: SchedulerProvider, val tag: String) {

    private val resultLivaData = MutableLiveData<Result<ResultType>>()

    private val result = MediatorLiveData<Result<ResultType>>()

    var disposable: Disposable? = null

    private var isExecuting = false

    fun execute(requestData: RequestType): LiveData<Result<ResultType>> {
        Timber.d("execute %s: %s", tag, isExecuting)
        if (isExecuting) {
            return resultLivaData
        }

        isExecuting = true
        resultLivaData.value = Result.loading()
        disposable = performAction(requestData)
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    Timber.d("ex success %s: %s", tag, it)
                    isExecuting = false
                    resultLivaData.value = Result.successFromRemote(it)
                    resultLivaData.value = null
                }, {
                    Timber.d("ex error %s: %s", tag, it)
                    isExecuting = false
                    resultLivaData.value = Result.errorFromRemote(it.localizedMessage)
                    resultLivaData.value = null
                    Timber.e(it)
                })

        return resultLivaData
    }

    protected abstract fun performAction(requestData: RequestType): Single<ResultType>
}