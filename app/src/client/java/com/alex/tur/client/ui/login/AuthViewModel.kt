package com.alex.tur.client.ui.login

import android.arch.lifecycle.*
import com.alex.tur.client.datamanager.auth.AuthDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.helper.Validator
import com.alex.tur.model.api.RequestLogin
import com.alex.tur.model.api.RequestSignUp
import com.alex.tur.scheduler.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel @Inject
constructor(
        private val authDataManager: AuthDataManager,
        private val validator: Validator): ViewModel() {

    init {
        Timber.tag("ViewModelFactory").i("ViewModel init ${hashCode()}")
    }

    private val loginTrigger = SingleLiveEvent<RequestLogin>()
    private val signUpTrigger = SingleLiveEvent<RequestSignUp>()

    val login: LiveData<Result<Boolean>> = Transformations.switchMap(loginTrigger, {
        authDataManager.login(it)
    })
    val signUp: LiveData<Result<Boolean>> = Transformations.switchMap(signUpTrigger, {
        authDataManager.signUp(it)
    })

    val signUpEmailValidation = SingleLiveEvent<String>()
    val signUpPasswordValidation = SingleLiveEvent<String>()
    val signUpNameValidation = SingleLiveEvent<String>()

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

    fun tryToSignUp(name: String, email: String, password: String) {
        val nameResult = validator.validateName(name)
        val emailResult = validator.validateEmail(email)
        val passwordResult = validator.validatePassword(password)

        signUpNameValidation.call()
        signUpEmailValidation.call()
        signUpPasswordValidation.call()

        var isValid = true

        if (nameResult.status == Result.Status.ERROR) {
            signUpNameValidation.value = nameResult.message
            isValid = false
        }

        if (emailResult.status == Result.Status.ERROR) {
            signUpEmailValidation.value = emailResult.message
            isValid = false
        }

        if (passwordResult.status == Result.Status.ERROR) {
            signUpPasswordValidation.value = passwordResult.message
            isValid = false
        }

        if (isValid) {
            signUpTrigger.value = RequestSignUp(name, email, password)
        }
    }
}