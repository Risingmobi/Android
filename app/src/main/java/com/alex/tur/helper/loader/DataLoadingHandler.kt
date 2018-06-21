package com.alex.tur.helper.loader

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.alex.tur.ext.launchSilent
import com.alex.tur.helper.Result
import com.alex.tur.utils.ThreadUtils
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import retrofit2.Call
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

abstract class DataLoadingHandler<ResultType> {

    private val ioContext: CoroutineContext = DefaultDispatcher
    private val uiContext: CoroutineContext = UI
    private val networkContext: CoroutineContext = newFixedThreadPoolContext(3, "networkIO")

    val result = MutableLiveData<Result<ResultType>>()

    @Volatile
    private var isExecuting = false

    fun execute(isForce: Boolean): LiveData<Result<ResultType>> {
        if (!isExecuting) {
            executeAsync(isForce)
        }
        return result
    }

    private fun executeAsync(isForce: Boolean) = launchSilent(uiContext) {
        Timber.tag("cortest").d("launchSilent 1 ${ThreadUtils.isMainThread()}")
        isExecuting = true

        if (result.value == null) {
            result.value = Result.initialLoading()

            fetchFromDb().await().also {
                result.value = Result.successFromLocal(it)
            }

            fetchFromNet().await().also {
                result.value = Result.successFromRemote(it)
            }

        } else {
            if (result.value?.status == Result.Status.ERROR || isForce) {
                result.value = Result.loading(result.value)
                fetchFromNet().await().also {
                    result.value = Result.successFromRemote(it)
                }
            }
        }

        isExecuting = false
    }

    private fun fetchFromDb() = async (ioContext) {
        try {
            loadFromDb()
        } catch (exc: Exception) {
            Timber.e(exc, "fetchFromDb")
            result.postValue(Result.errorFromLocal(exc.localizedMessage, result.value?.data))
            null
        }
    }

    private fun fetchFromNet() = async(ioContext) {
        try {
            loadFromNet().execute().let {
                saveFromNet(it.body())
                it.body()
            }
        } catch (exc: Exception) {
            Timber.e(exc, "fetchFromNet")
            result.postValue(Result.errorFromRemote(exc.localizedMessage, result.value?.data))
            null
        }
    }


    fun refreshFromLocal() = launchSilent(uiContext) {
        if(!isExecuting) {
            fetchFromDb().await().also { data ->
                result.value?.also { res ->
                    result.value = res.reSetData(data)
                }
            }
        }
    }



    protected abstract fun loadFromDb(): ResultType?
    protected abstract fun loadFromNet(): Call<ResultType>
    protected abstract fun saveFromNet(data: ResultType?)
}