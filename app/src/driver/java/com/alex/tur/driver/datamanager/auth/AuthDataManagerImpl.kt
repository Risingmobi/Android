package com.alex.tur.driver.datamanager.auth

import android.arch.lifecycle.LiveData
import com.alex.tur.AppConstants
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.driver.repo.auth.DriverAuthApiRepository
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.model.api.RequestLogin
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataManagerImpl
@Inject constructor(
        private val authApiRepository: DriverAuthApiRepository,
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider
): DriverAuthDataManager {

    private val loginActionHandler = object : SingleActionHandler<RequestLogin, Unit>(schedulerProvider, "LOGIN") {
        override fun performAction(requestData: RequestLogin): Single<Unit> {
            return authApiRepository.login(AppConstants.getAuthHeaderForLogin(), AppConstants.OAUTH_GRANT_TYPE, requestData.email, requestData.password)
                    .map {
                        myAccountManager.login(requestData.email, requestData.password, it)
                    }
        }
    }

    override fun login(requestLogin: RequestLogin): LiveData<Result<Unit>> {
        return loginActionHandler.execute(requestLogin)
    }
}