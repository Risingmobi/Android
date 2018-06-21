package com.alex.tur.client.datamanager.profile

import android.arch.lifecycle.LiveData
import com.alex.tur.client.repo.auth.AuthApiRepository
import com.alex.tur.client.repo.profile.ClientProfileApiRepository
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.SingleDataLoadingHandler
import com.alex.tur.helper.Result
import com.alex.tur.helper.loader.DataLoadingHandler
import com.alex.tur.model.Customer
import com.alex.tur.model.MyAddress
import com.alex.tur.scheduler.SchedulerProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.RequestBody
import okhttp3.MultipartBody
import retrofit2.Call
import java.io.File

@Singleton
class ProfileDataManagerImpl
@Inject constructor(
        private val authApiRepository: AuthApiRepository,
        private val profileApiRepository: ClientProfileApiRepository,
        private val myAccountManager: MyAccountManager,
        private val addressFetcher: AddressFetcher,
        private val schedulerProvider: SchedulerProvider
): ProfileDataManager {

    /*private val profileLoader = object: SingleDataLoadingHandler<Customer>(schedulerProvider, "PROFILE") {

        override fun loadFromDb(params: Map<String, String>?): Single<Customer> {
            return myAccountManager.getClientProfile()
        }

        override fun loadFromNet(params: Map<String, String>?): Single<Customer> {
            return profileApiRepository.getProfile(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: Customer) {
            data.addressString = addressFetcher.fetchAddress(data.lat, data.lng, MyAddress.Type.HOME)?.addressString
            myAccountManager.updateCustomerProfile(data)
        }
    }*/

    private val profileLoader = object : DataLoadingHandler<Customer>() {

        override fun loadFromDb(): Customer? {
            return myAccountManager.getClientProfile()
        }

        override fun loadFromNet(): Call<Customer> {
            return profileApiRepository.getProfileTest(myAccountManager.getAuthHeader())
        }

        override fun saveFromNet(data: Customer?) {
            data?.also { customer ->
                data.addressString = addressFetcher.fetchAddress(customer.lat, customer.lng, MyAddress.Type.HOME)?.addressString
                myAccountManager.updateCustomerProfile(customer)
            }
        }
    }

    override fun getProfile(isForce: Boolean): LiveData<Result<Customer>> {
        Timber.tag("cortest").d("getProfile")
        return profileLoader.execute(isForce)
    }



    override fun changeName(name: String): Completable {
        val customer = Customer()
        customer.name = name
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), customer)
                .doOnSuccess {
                    myAccountManager.changeName(it.name)
                    profileLoader.refreshFromLocal()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changeEmail(email: String): Completable {
        val customer = Customer()
        customer.email = email
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), customer)
                .doOnSuccess {
                    myAccountManager.changeEmail(it.email)
                    profileLoader.refreshFromLocal()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changePassword(password: String): Completable {
        val customer = Customer()
        customer.password = password
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), customer)
                .doOnSuccess {
                    myAccountManager.changePassword(it.password)
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changeAddress(latLng: LatLng): Completable {
        val customer = Customer()
        customer.lat = latLng.latitude
        customer.lng = latLng.longitude
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), customer)
                .doOnSuccess {
                    it.addressString =  addressFetcher.fetchAddress(it.lat, it.lng, MyAddress.Type.HOME)?.addressString
                    myAccountManager.changeAddress(it.lat, it.lng, it.addressString!!)
                    profileLoader.refreshFromLocal()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changeAvatar(avatar: String): Completable {
        val customer =  Customer()
        customer.avatar = avatar

        val file = File(avatar)

        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
        Timber.d("file %s", file)

        return profileApiRepository.updateAvatar(myAccountManager.getAuthHeader(), part)
                .doOnSuccess {
                    myAccountManager.changeAvatar(it.avatar)
                    profileLoader.refreshFromLocal()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun changePhone(phone: String): Completable {
        val customer = Customer()
        customer.phone = phone
        return profileApiRepository.updateProfile(myAccountManager.getAuthHeader(), customer)
                .doOnSuccess {
                    myAccountManager.changePhone(it.phone)
                    profileLoader.refreshFromLocal()
                }
                .observeOn(schedulerProvider.ui())
                .toCompletable()
    }

    override fun logout(): Completable {
        return Completable.fromCallable {
            myAccountManager.logout()
        }
    }
}