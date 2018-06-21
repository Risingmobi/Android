package com.alex.tur.driver.ui.profile.edit_car

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.alex.tur.base.BaseViewModel
import com.alex.tur.driver.datamanager.profile.DriverProfileDataManager
import com.alex.tur.helper.Result
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.Driver
import javax.inject.Inject

class EditCarViewModel @Inject constructor(
        private val profileDataManager: DriverProfileDataManager
): BaseViewModel() {

    private val modelValidation = SingleLiveEvent<String>()
    private val numberValidation = SingleLiveEvent<String>()
    private val saveTrigger = SingleLiveEvent<Driver>()
    val saveAction: LiveData<Result<Driver>> = Transformations.switchMap(saveTrigger, {
        profileDataManager.changeCar(it)
    })

    fun onSaveClicked(model: String, number: String) {
        modelValidation.call()
        numberValidation.call()

        var isValid = true

        if (model.isBlank()) {
            modelValidation.value = "Model can not be empty"
            isValid = false
        }

        if (number.isBlank()) {
            numberValidation.value = "Number can not be empty"
            isValid = false
        }

        if (isValid) {
            val driver = Driver()
            driver.vehicleModel = model
            driver.vehicleNumber = number
            saveTrigger.value = driver
        }
    }
}