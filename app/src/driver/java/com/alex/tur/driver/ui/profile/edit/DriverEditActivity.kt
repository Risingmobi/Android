package com.alex.tur.driver.ui.profile.edit

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.helper.Result
import com.alex.tur.ui.profile.edit.EditActivity
import com.alex.tur.ui.profile.edit.QualifierEditParam
import com.alex.tur.ui.profile.edit.QualifierEditType
import javax.inject.Inject

class DriverEditActivity: EditActivity() {

    @Inject
    @JvmField
    @field:QualifierEditType
    var editType: String? = null

    @Inject
    @JvmField
    @field:QualifierEditParam
    var editParam: String? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<DriverEditViewModel>

    lateinit var viewModel: DriverEditViewModel

    companion object {

        fun start(activity: Activity?, editType: String, editParam: String?) {
            activity?.startActivity(Intent(activity, DriverEditActivity::class.java).apply {
                putExtra(EXTRA_EDIT_TYPE, editType)
                putExtra(EXTRA_EDIT_PARAM, editParam)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DriverEditViewModel::class.java)

        viewModel.action.observe(this, Observer {
            it?.let {
                when(it.status) {
                    Result.Status.LOADING -> {
                        showLoading()
                    }
                    Result.Status.ERROR -> {
                        hideLoading()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Result.Status.SUCCESS -> {
                        finish()
                    }
                }
            }
        })
    }

    override fun getEditType(): String? {
        return editType
    }

    override fun getEditParam(): String? {
        return editParam
    }

    override fun onEditNameClicked(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditEmailClicked(email: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditPasswordClicked(password: String) {
        viewModel.onEditPasswordClicked(password)
    }

    override fun onEditPhoneClicked(phone: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}