package com.alex.tur.client.ui.profile.edit

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

class ClientEditActivity: EditActivity() {

    @Inject
    @JvmField
    @field:QualifierEditType
    var editType: String? = null

    @Inject
    @JvmField
    @field:QualifierEditParam
    var editParam: String? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ClientEditViewModel>

    lateinit var viewModel: ClientEditViewModel

    companion object {

        fun start(activity: Activity?, editType: String, editParam: String?) {
            activity ?: return
            val intent = Intent(activity, ClientEditActivity::class.java)
            intent.putExtra(EXTRA_EDIT_TYPE, editType)
            intent.putExtra(EXTRA_EDIT_PARAM, editParam)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientEditViewModel::class.java)

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
        viewModel.onEditNameClicked(name)
    }

    override fun onEditEmailClicked(email: String) {
        viewModel.onEditEmailClicked(email)
    }

    override fun onEditPasswordClicked(password: String) {
        viewModel.onEditPasswordClicked(password)
    }

    override fun onEditPhoneClicked(phone: String) {
        viewModel.onEditPhoneClicked(phone)
    }
}