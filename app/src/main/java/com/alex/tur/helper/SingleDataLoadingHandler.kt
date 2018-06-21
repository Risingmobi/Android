package com.alex.tur.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber
//TODO implement coroutines
abstract class SingleDataLoadingHandler<ResultType>(private val schedulerProvider: SchedulerProvider, private val tag: String) {

    val resultLiveData = MediatorLiveData<Result<ResultType>>()

    @Volatile
    private var isExecuting = false

    private var disposable: Disposable? = null

    fun execute(isForce: Boolean = false, paramsDB: Map<String, String>? = null, paramsNET: Map<String, String>? = null): LiveData<Result<ResultType>> {
        if (isExecuting) {
            return resultLiveData
        }

        if (resultLiveData.value == null) {
            isExecuting = true
            resultLiveData.value = Result.initialLoading()
            getDbSingle(paramsDB)?.let { dbSingle ->
                disposable = dbSingle
                        .subscribe({
                            disposable = getNetSingle(paramsNET)
                                    .subscribe({

                                    }, {
                                        Timber.e(it, "initialLoading net %s", tag)
                                    })
                        }, {
                            disposable = getNetSingle(paramsNET)
                                    .subscribe({

                                    }, {
                                        Timber.e(it, "initialLoading net %s", tag)
                                    })
                            Timber.e(it, "initialLoading db %s", tag)
                        })
            }?: run {
                disposable = getNetSingle(paramsNET)
                        .subscribe({ }, {Timber.e(it, "initialLoading net %s", tag)})
            }
        } else {
            resultLiveData.value?.let { result ->
                if (result.status == Result.Status.ERROR || isForce) {
                    isExecuting = true
                    resultLiveData.value = Result.loading(result)
                    disposable = getNetSingle(paramsNET)
                            .subscribe({ }, {Timber.e(it, "refresh net %s", tag)})
                } else {
                    return resultLiveData
                }
            }
        }
        return resultLiveData
    }

    private fun getDbSingle(params: Map<String, String>?): Single<ResultType>? {
        return loadFromDb(params)?.let { single ->
            single.subscribeOn(schedulerProvider.computation())
                    .observeOn(schedulerProvider.ui())
                    .doOnSuccess {
                        Timber.tag("SingleLoader").e("ex %s db success", tag)
                        resultLiveData.value = Result.successFromLocal(it)
                    }
                    .doOnError {
                        isExecuting = false
                        Timber.e(it, "getDbSingle error %s", tag)
                        Timber.tag("SingleLoader").e("ex %s db error: %s", tag, it)
                        if (it?.message == "The callable returned a null value") {
                            resultLiveData.value = Result.errorFromLocal(null, resultLiveData.value?.data)
                        } else {
                            resultLiveData.value = Result.errorFromLocal(it.localizedMessage, resultLiveData.value?.data)
                        }
                    }
        }
    }

    private fun getNetSingle(params: Map<String, String>? = null): Single<ResultType> {
        return loadFromNet(params)
                .observeOn(schedulerProvider.computation())
                .map {
                    saveFromNet(it)
                    it
                }
                .observeOn(schedulerProvider.ui())
                .doOnSuccess {
                    isExecuting = false
                    Timber.tag("SingleLoader").e("ex %s net success", tag)
                    resultLiveData.value = Result.successFromRemote(it)
                }
                .doOnError {
                    isExecuting = false
                    Timber.tag("SingleLoader").e("ex %s net error: %s", tag, it)
                    if (it?.message == "The callable returned a null value") {
                        resultLiveData.value = Result.errorFromRemote(null, resultLiveData.value?.data)
                    } else {
                        resultLiveData.value = Result.errorFromRemote(it.localizedMessage, resultLiveData.value?.data)
                    }
                }
    }

    fun refreshDataForAllSubscribers(params: Map<String, String>? = null) {
        if(!isExecuting) {
            loadFromDb(params)?.subscribeOn(schedulerProvider.io())?.observeOn(schedulerProvider.ui())?.doOnSuccess { data ->
                resultLiveData.value?.also { result ->
                    resultLiveData.value = result.reSetData(data)
                }
            }?.subscribe()
        }
    }

    protected abstract fun loadFromDb(params: Map<String, String>?): Single<ResultType>?
    protected abstract fun loadFromNet(params: Map<String, String>?): Single<ResultType>
    protected abstract fun saveFromNet(data: ResultType)
}
