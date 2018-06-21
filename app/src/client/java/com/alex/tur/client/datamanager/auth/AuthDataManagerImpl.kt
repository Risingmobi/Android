package com.alex.tur.client.datamanager.auth

import android.arch.lifecycle.LiveData
import com.alex.tur.AppConstants
import com.alex.tur.client.repo.auth.AuthApiRepository
import com.alex.tur.data.auth.MyAccountManager
import com.alex.tur.data.pref.PrefManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleActionHandler
import com.alex.tur.model.api.RequestLogin
import com.alex.tur.model.api.RequestSignUp
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataManagerImpl
@Inject constructor(
        private val authApiRepository: AuthApiRepository,
        private val myAccountManager: MyAccountManager,
        private val schedulerProvider: SchedulerProvider,
        private val prefManager: PrefManager
): AuthDataManager {

    private val signUpActionHandler = object : SingleActionHandler<RequestSignUp, Boolean>(schedulerProvider, "SIGN_UP") {
        override fun performAction(requestData: RequestSignUp): Single<Boolean> {
            return authApiRepository.signUp(requestData)
                    .flatMap {
                        myAccountManager.signUp(requestData.name, requestData.email, requestData.password)
                        authApiRepository.login(AppConstants.getAuthHeaderForLogin(), AppConstants.OAUTH_GRANT_TYPE, requestData.email, requestData.password)
                    }
                    .map {
                        myAccountManager.login(requestData.email, requestData.password, it)
                        val isFirstLaunch = prefManager.isFirstLaunch()
                        prefManager.setFirstLaunch(false)
                        isFirstLaunch
                    }
        }
    }

    private val loginActionHandler = object : SingleActionHandler<RequestLogin, Boolean>(schedulerProvider, "LOGIN") {
        override fun performAction(requestData: RequestLogin): Single<Boolean> {
            return authApiRepository.login(AppConstants.getAuthHeaderForLogin(), AppConstants.OAUTH_GRANT_TYPE, requestData.email, requestData.password)
                    .map {
                        myAccountManager.login(requestData.email, requestData.password, it)
                        val isFirstLaunch = prefManager.isFirstLaunch()
                        prefManager.setFirstLaunch(false)
                        isFirstLaunch
                    }
        }
    }

    override fun signUp(requestSignUp: RequestSignUp): LiveData<Result<Boolean>> {
        return signUpActionHandler.execute(requestSignUp)
    }

    override fun login(requestLogin: RequestLogin): LiveData<Result<Boolean>> {
        return loginActionHandler.execute(requestLogin)
    }
}