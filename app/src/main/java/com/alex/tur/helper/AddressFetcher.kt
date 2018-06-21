package com.alex.tur.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.model.MyAddress
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressFetcher @Inject constructor(
        @QualifierAppContext private val context: Context
) {
    fun fetchAddressAsync(location: Location?, type: MyAddress.Type): Single<MyAddress?> {
        return fetchAddressAsync(location?.latitude, location?.longitude, type)
    }

    fun fetchAddressAsync(latLng: LatLng?, type: MyAddress.Type): Single<MyAddress?> {
        return fetchAddressAsync(latLng?.latitude, latLng?.longitude, type)
    }

    fun fetchAddressAsync(latitude: Double?, longitude: Double?, type: MyAddress.Type): Single<MyAddress?> {
        return Single.fromCallable {
            if (latitude == null || longitude == null) {
                return@fromCallable null
            }
            val geocoder = Geocoder(context)
            Timber.i("fetch $latitude, $longitude")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
                return@fromCallable MyAddress(LatLng(latitude, longitude), type, fetchAddressString(addresses[0]))
            } else {
                return@fromCallable null
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAddress(location: Location?, type: MyAddress.Type): MyAddress? {
        return fetchAddress(location?.latitude, location?.longitude, type)
    }

    fun fetchAddress(latLng: LatLng?, type: MyAddress.Type): MyAddress? {
        return fetchAddress(latLng?.latitude, latLng?.longitude, type)
    }

    fun fetchAddress(latitude: Double?, longitude: Double?, type: MyAddress.Type): MyAddress? {
        if (latitude == null || longitude == null) {
            return null
        }
        val geocoder = Geocoder(context)

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses != null && addresses.size > 0) {
            MyAddress(LatLng(latitude, longitude), type, fetchAddressString(addresses[0]))
        } else {
            null
        }
    }





    private fun fetchAddressString(address: Address?): String {
        return if (address == null || address.thoroughfare == "Unnamed Road" || address.thoroughfare == "Unnamed Road Unnamed Road") {
            "Unknown address"
        } else {
//            Timber.w("address")
//            Timber.d("thoroughfare %s", address.thoroughfare)
//            Timber.d("adminArea %s", address.adminArea)
//            Timber.d("countryName %s", address.countryName)
//            Timber.d("featureName %s", address.featureName)
//            Timber.d("subAdminArea %s", address.subAdminArea)
//            Timber.d("subLocality %s", address.subLocality)
//            Timber.d("locality %s", address.locality)

            val lines = mutableListOf<String>()

            if (address.featureName != null) {
                lines.add(address.featureName)
            }

            if (address.thoroughfare != null) {
                lines.add(address.thoroughfare)
            }

            var addressString = ""

            for (i in lines.indices) {
                addressString += lines[i]
                if (i < lines.size - 1) {
                    addressString += ", "
                }
            }

            return addressString
        }
    }
}
