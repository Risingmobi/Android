package com.alex.tur.model

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

class MyAddress: Parcelable {

    val latLng: LatLng

    var addressString: String? = null

    val type: Type

    constructor(latLng: LatLng, type: Type, addressString: String?) {
        this.latLng = latLng
        this.type = type
        this.addressString = addressString
    }

    constructor(location: Location, type: Type, addressString: String?) {
        this.latLng = LatLng(location.latitude, location.longitude)
        this.type = type
        this.addressString = addressString
    }

    private constructor(parcel: Parcel) : this(
            parcel.readParcelable(LatLng::class.java.classLoader) as LatLng,
            Type.valueOf(parcel.readString()),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(latLng, flags)
        parcel.writeString(type.toString())
        parcel.writeString(addressString)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "MyAddress(latLng=$latLng, addressString=$addressString, type=$type)"
    }


    companion object {

        @JvmField
        var CREATOR: Parcelable.Creator<MyAddress> = object: Parcelable.Creator<MyAddress> {

            override fun createFromParcel(parcel: Parcel): MyAddress {
                return MyAddress(parcel)
            }

            override fun newArray(size: Int): Array<MyAddress?> {
                return arrayOfNulls(size)
            }
        }

        fun home(latLng: LatLng, addressString: String? = null): MyAddress {
            return MyAddress(latLng, Type.HOME, addressString)
        }

        fun home(location: Location, addressString: String? = null): MyAddress {
            return MyAddress(location, Type.HOME, addressString)
        }

        fun current(latLng: LatLng, addressString: String? = null): MyAddress {
            return MyAddress(latLng, Type.CURRENT, addressString)
        }

        fun current(location: Location, addressString: String? = null): MyAddress {
            return MyAddress(location, Type.CURRENT, addressString)
        }

        fun selected(latLng: LatLng, addressString: String? = null): MyAddress {
            return MyAddress(latLng, Type.SELECTED, addressString)
        }

        fun selected(location: Location, addressString: String? = null): MyAddress {
            return MyAddress(location, Type.SELECTED, addressString)
        }

        fun none(latLng: LatLng, addressString: String? = null): MyAddress {
            return MyAddress(latLng, Type.NONE, addressString)
        }

        fun none(location: Location, addressString: String? = null): MyAddress {
            return MyAddress(location, Type.NONE, addressString)
        }
    }

    enum class Type {
        HOME, CURRENT, SELECTED, NONE
    }


}