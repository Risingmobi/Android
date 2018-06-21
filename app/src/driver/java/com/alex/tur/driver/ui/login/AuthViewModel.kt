package com.alex.tur.driver.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.alex.tur.driver.datamanager.auth.DriverAuthDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.helper.Validator
import com.alex.tur.model.api.RequestLogin
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel @Inject
constructor(
        private val authDataManager: DriverAuthDataManager,
        private val validator: Validator
): ViewModel() {

    private val loginTrigger = SingleLiveEvent<RequestLogin>()

    val login: LiveData<Result<Unit>> = Transformations.switchMap(loginTrigger, {
        authDataManager.login(it)
    })

    val loginEmailValidation = SingleLiveEvent<String>()
    val loginPasswordValidation = SingleLiveEvent<String>()

    fun tryToLogin(email: String, password: String) {
        val emailResult = validator.validateEmail(email)
        val passwordResult = validator.validatePassword(password)

        loginEmailValidation.call()
        loginPasswordValidation.call()

        var isValid = true

        if (emailResult.status == Result.Status.ERROR) {
            loginEmailValidation.value = emailResult.message
            isValid = false
        }

        if (passwordResult.status == Result.Status.ERROR) {
            loginPasswordValidation.value = passwordResult.message
            isValid = false
        }

        if (isValid) {
            loginTrigger.value = RequestLogin(email, password)
        }
    }
}