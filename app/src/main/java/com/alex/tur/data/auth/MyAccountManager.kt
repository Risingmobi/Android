package com.alex.tur.data.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import com.alex.tur.AppConstants
import com.alex.tur.converters.DriverServiceListConverter
import com.alex.tur.converters.DriverStatusConverter
import com.alex.tur.converters.DriverTransportModeConverter
import com.alex.tur.di.qualifier.global.QualifierAccountType
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.model.*
import com.alex.tur.model.api.ResponseToken
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyAccountManager
@Inject constructor(@QualifierAppContext private val context: Context,
                    @QualifierAccountType private val accountType: String) {

    companion object {
        private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
        private const val KEY_TOKEN_ADD_TIME_MILLIS = "KEY_TOKEN_ADD_TIME_MILLIS"
        private const val KEY_TOKEN_EXPIRES_MILLIS = "KEY_TOKEN_EXPIRES_MILLIS"
        private const val KEY_TOKEN_SCOPE = "KEY_TOKEN_SCOPE" /* "read write" */

        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_EMAIL = "KEY_USER_EMAIL"
        private const val KEY_USER_AVATAR_URL = "KEY_USER_AVATAR_URL"

        private const val KEY_USER_LAT = "KEY_USER_LAT"
        private const val KEY_USER_LNG = "KEY_USER_LNG"
        private const val KEY_USER_PHONE = "KEY_USER_PHONE"
        private const val KEY_USER_ADDRESS_STRING = "KEY_USER_ADDRESS_STRING"

        private const val KEY_DRIVER_VEHICLE_NUMBER = "KEY_DRIVER_VEHICLE_NUMBER"
        private const val KEY_DRIVER_VEHICLE_MODEL = "KEY_DRIVER_VEHICLE_MODEL"
        private const val KEY_DRIVER_STATUS = "KEY_DRIVER_STATUS"
        private const val KEY_DRIVER_TRANSPORT_MODE = "KEY_DRIVER_TRANSPORT_MODE"
        private const val KEY_DRIVER_SERVICES = "KEY_DRIVER_SERVICES"
        private const val KEY_DRIVER_COMPANY_NAME = "KEY_DRIVER_COMPANY_NAME"
        private const val KEY_DRIVER_COMPANY_PICTURE = "KEY_DRIVER_COMPANY_PICTURE"
    }

    fun signUp(name: String, email: String, password: String) {
        val account = Account(email, accountType)

        val userData = Bundle()
        userData.putString(KEY_USER_NAME, name)
        userData.putString(KEY_USER_EMAIL, email)

        val accountManager = AccountManager.get(context)
        val added = accountManager.addAccountExplicitly(account, password, userData)
        Timber.d("signUp %s, %s", account, added)
    }

    fun login(email: String, password: String, responseToken: ResponseToken) {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        var account: Account? = null
        for (acc in accounts) {
            if (acc.name == email) {
                account = acc
                Timber.d("login account found %s", acc)
                break
            }
        }

        if (account == null) {
            account = Account(email, accountType)
            val added = accountManager.addAccountExplicitly(account, password, null)
            Timber.d("login account not found, created %s, %s", account, added)
        }

        val currentTimeMillis: Long = System.currentTimeMillis()
        val expiresTimeMillis = responseToken.expiresIn?.times(1000)

        accountManager.setAuthToken(account, responseToken.tokenType, responseToken.accessToken)
        accountManager.setUserData(account, KEY_REFRESH_TOKEN, responseToken.refreshToken)
        accountManager.setUserData(account, KEY_USER_EMAIL, email)
        accountManager.setUserData(account, KEY_TOKEN_ADD_TIME_MILLIS, currentTimeMillis.toString())
        accountManager.setUserData(account, KEY_TOKEN_EXPIRES_MILLIS, expiresTimeMillis.toString())
        accountManager.setUserData(account, KEY_TOKEN_SCOPE, responseToken.scope)
    }

    fun logout() {
        val accountManager = AccountManager.get(context)

        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()

        account?.let {
            accountManager.removeAccount(account,  {
                Timber.d("removeAccount complete %s", it.result)
            }, null)
        }
    }

    fun getAuthToken(): String? {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()
        return account?.let {
            accountManager.peekAuthToken(account, AppConstants.OAUTH_TOKEN_TYPE)
        }
    }

    fun getAuthHeader(): String {
        return "Bearer ${getAuthToken()}"
    }

    fun updateCustomerProfile(customer: Customer) {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()
        account?.let {
            accountManager.setUserData(account, KEY_USER_NAME, customer.name)
            accountManager.setUserData(account, KEY_USER_EMAIL, customer.email)
            accountManager.setUserData(account, KEY_USER_AVATAR_URL, customer.avatar)
            accountManager.setUserData(account, KEY_USER_LAT, customer.lat?.toString())
            accountManager.setUserData(account, KEY_USER_LNG, customer.lng?.toString())
            accountManager.setUserData(account, KEY_USER_PHONE, customer.phone)
            accountManager.setUserData(account, KEY_USER_ADDRESS_STRING, customer.addressString)
        }
    }

    fun updateDriverProfile(driver: Driver) {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()
        account?.let {
            accountManager.setUserData(account, KEY_USER_NAME, driver.name)
            accountManager.setUserData(account, KEY_USER_EMAIL, driver.email)
            accountManager.setUserData(account, KEY_USER_AVATAR_URL, driver.avatar)
            accountManager.setUserData(account, KEY_USER_LAT, driver.lat?.toString())
            accountManager.setUserData(account, KEY_USER_LNG, driver.lng?.toString())
            accountManager.setUserData(account, KEY_USER_PHONE, driver.phone)

            accountManager.setUserData(account, KEY_DRIVER_VEHICLE_NUMBER, driver.vehicleNumber)
            accountManager.setUserData(account, KEY_DRIVER_VEHICLE_MODEL, driver.vehicleModel)
            accountManager.setUserData(account, KEY_DRIVER_STATUS, driver.status.toString())
            accountManager.setUserData(account, KEY_DRIVER_TRANSPORT_MODE, driver.transportMode.toString())
            accountManager.setUserData(account, KEY_DRIVER_SERVICES, DriverServiceListConverter().toDb(driver.availableServices))
            accountManager.setUserData(account, KEY_DRIVER_COMPANY_NAME, driver.company?.naming)
            accountManager.setUserData(account, KEY_DRIVER_COMPANY_PICTURE, driver.company?.picture)
        }
    }

    fun getClientProfile(): Customer {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()

//            val password = accountManager.getPassword(account)
        return Customer().apply {
            name = accountManager.getUserData(account, KEY_USER_NAME)
            email = accountManager.getUserData(account, KEY_USER_EMAIL)
            avatar = accountManager.getUserData(account, KEY_USER_AVATAR_URL)
            lat = accountManager.getUserData(account, KEY_USER_LAT)?.toDouble()
            lng = accountManager.getUserData(account, KEY_USER_LNG)?.toDouble()
            phone = accountManager.getUserData(account, KEY_USER_PHONE)
            addressString = accountManager.getUserData(account, KEY_USER_ADDRESS_STRING)
        }
    }

    fun getDriverProfile(): Single<Driver> {
        return Single.fromCallable {
            val accountManager = AccountManager.get(context)
            val accounts = accountManager.getAccountsByType(accountType)
            val account = accounts.firstOrNull()

//            val password = accountManager.getPassword(account)

            val company = Company().apply {
                naming = accountManager.getUserData(account, KEY_DRIVER_COMPANY_NAME)
                picture = accountManager.getUserData(account, KEY_DRIVER_COMPANY_PICTURE)
            }

            val driver = Driver().apply {
                name = accountManager.getUserData(account, KEY_USER_NAME)
                email = accountManager.getUserData(account, KEY_USER_EMAIL)
                avatar = accountManager.getUserData(account, KEY_USER_AVATAR_URL)
                lat = accountManager.getUserData(account, KEY_USER_LAT)?.toDouble()
                lng = accountManager.getUserData(account, KEY_USER_LNG)?.toDouble()
                phone = accountManager.getUserData(account, KEY_USER_PHONE)
                vehicleNumber = accountManager.getUserData(account, KEY_DRIVER_VEHICLE_NUMBER)
                vehicleModel = accountManager.getUserData(account, KEY_DRIVER_VEHICLE_MODEL)
                status = DriverStatusConverter().fromDb(accountManager.getUserData(account, KEY_DRIVER_STATUS))
                transportMode = DriverTransportModeConverter().fromDb(accountManager.getUserData(account, KEY_DRIVER_TRANSPORT_MODE))
                availableServices = DriverServiceListConverter().fromDb(accountManager.getUserData(account, KEY_DRIVER_SERVICES))
                this.company = company
            }
            Timber.tag("fgbfbhgfh").d("MyAccountManager getDriverProfile %s", driver)
            driver
        }
    }

    fun getDriverTransportMode(): DriverTransportMode? {
        return getAccount()?.let {
            val accountManager = AccountManager.get(context)
            DriverTransportModeConverter().fromDb(accountManager.getUserData(it, KEY_DRIVER_TRANSPORT_MODE))
        }
    }

    fun hasAccount(): Boolean {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        val account = accounts.firstOrNull()
        val hasAccount = account != null
        Timber.d("hasAccount %s", hasAccount)
        return hasAccount
    }

    fun changeName(name: String?) {
        name?: return
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_USER_NAME, name)
        }
    }

    fun changeEmail(email: String?) {
        email?: return
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_USER_EMAIL, email)
        }
    }

    fun changePassword(password: String?) {
        password?: return
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setPassword(it, password)
        }
    }

    private fun getAccount(): Account? {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(accountType)
        return accounts.firstOrNull()
    }

    fun changeAddress(lat: Double?, lng: Double?, addressString: String) {
        Timber.d("changeAddress %s", addressString)
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_USER_LAT, lat.toString())
            accountManager.setUserData(it, KEY_USER_LNG, lng.toString())
            accountManager.setUserData(it, KEY_USER_ADDRESS_STRING, addressString)
        }
    }

    fun changeAvatar(avatar: String?) {
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_USER_AVATAR_URL, avatar)
        }
    }

    fun changeStatus(status: DriverStatus?) {
        status?: return
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_DRIVER_STATUS, status.toString())
        }
    }

    fun changeTransportMode(transportMode: DriverTransportMode?) {
        transportMode?: return
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_DRIVER_TRANSPORT_MODE, transportMode.toString())
        }
    }

    fun changeAvailableServices(availableServices: MutableList<Service>?) {
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_DRIVER_SERVICES, DriverServiceListConverter().toDb(availableServices))
        }
    }

    fun changePhone(phone: String?) {
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_USER_PHONE, phone)
        }
    }

    fun changeVehicleModel(vehicleModel: String?) {
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_DRIVER_VEHICLE_MODEL, vehicleModel)
        }
    }

    fun changeVehicleNumber(vehicleNumber: String?) {
        getAccount()?.let {
            val accountManager = AccountManager.get(context)
            accountManager.setUserData(it, KEY_DRIVER_VEHICLE_NUMBER, vehicleNumber)
        }
    }
}