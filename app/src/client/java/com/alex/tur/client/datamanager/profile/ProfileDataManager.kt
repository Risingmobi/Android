package com.alex.tur.client.datamanager.profile

import android.arch.lifecycle.LiveData
import com.alex.tur.helper.Result
import com.alex.tur.model.Customer
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable

interface ProfileDataManager {
    fun getProfile(isForce: Boolean): LiveData<Result<Customer>>
    fun logout(): Completable
    fun changeName(name: String): Completable
    fun changeEmail(email: String): Completable
    fun changePassword(password: String): Completable
    fun changeAddress(latLng: LatLng): Completable
    fun changeAvatar(avatar: String): Completable
    fun changePhone(phone: String): Completable
}