package com.alex.tur.client.datamanager.addresssearch

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import com.alex.tur.client.ui.address.search.AddressItem
import com.alex.tur.client.ui.address.search.FoundAddressItem
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.scheduler.SchedulerProvider
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.*
import com.google.android.gms.tasks.Tasks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PlacesManager @Inject constructor(
        @QualifierAppContext val context: Context,
        val schedulerProvider: SchedulerProvider
): LifecycleObserver {

    private var mGeoDataClient: GeoDataClient? = null

    private var queryDisposable: Disposable? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mGeoDataClient = Places.getGeoDataClient(context)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        queryDisposable?.dispose()
    }



    fun setQuery(query: String, callback: Callback) {
        queryDisposable?.dispose()
        callback.loading(true)
        queryDisposable = Observable.fromCallable {
            val results = mGeoDataClient?.getAutocompletePredictions(query, null, AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build())
            if (results != null) {
                Tasks.await(results, 30, TimeUnit.SECONDS)
                DataBufferUtils.freezeAndClose(results.result)
            } else {
                ArrayList<AutocompletePrediction>()
            }
        }.map {
            val addressList = mutableListOf<AddressItem>()
            for (prediction in it) {
                val fullText = prediction.getFullText(null)
                val placeId = prediction.placeId
                if (fullText != null && placeId != null) {
                    addressList.add(FoundAddressItem(fullText.toString(), placeId))
                }
            }
            addressList
        }
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.ui())
        .subscribe({
            callback.loading(false)
            callback.result(it)
        }, {
            callback.loading(false)
            Timber.e(it)
        })
    }

    fun fetchPlaceDetail(placeId: String?, function: (Place) -> Unit) {
        placeId?.let {
            mGeoDataClient?.getPlaceById(placeId)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val places = it.result as PlaceBufferResponse
                    val myPlace = places.get(0)
                    function(myPlace)
                    places.release()
                } else {
                    Timber.e("Not found")
                }
            }
        }
    }

    interface Callback {
        fun loading(isLoading: Boolean)
        fun result(result: MutableList<AddressItem>)
    }
}