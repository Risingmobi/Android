package com.alex.tur.base

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.alex.tur.helper.LoadingUiHelper
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber

abstract class BaseActivity : DaggerAppCompatActivity() {

    private var progressDialog: LoadingUiHelper.ProgressDialogFragment? = null

    companion object {
        private const val RC_LOCATION_PERMISSION = 50235
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = supportFragmentManager.findFragmentByTag(LoadingUiHelper.ProgressDialogFragment.TAG)
                as LoadingUiHelper.ProgressDialogFragment?
    }

    fun showLoading(type : LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        if(progressDialog == null) {
            progressDialog = LoadingUiHelper.showProgress(supportFragmentManager, type)
        }
    }

    fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                Timber.d("onRequestPermissionsResult %s, %s", grantResults[0], grantResults[1])
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionLocationGranted()
                } else {
                    onPermissionLocationDenied()
                }
            }
        }
    }

    protected open fun onPermissionLocationDenied() {}

    protected open fun onPermissionLocationGranted() {}

    fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), RC_LOCATION_PERMISSION)
    }

    fun showError(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun checkStoragePermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED
    }
}
