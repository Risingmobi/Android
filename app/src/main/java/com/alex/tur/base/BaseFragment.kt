package com.alex.tur.base

import android.Manifest.permission.*
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import com.alex.tur.helper.LoadingUiHelper
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {

    private var baseActivity: BaseActivity? = null

    val viewLifecycleOwner = ViewLifecycleOwner()

    companion object {
        private const val RC_LOCATION_PERMISSION = 50123
        private const val RC_READ_EXTERNAL_STORAGE_PERMISSION = 50124
        private const val RC_CAMERA_PERMISSION = 50125
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.baseActivity = context as BaseActivity?
    }

    fun getViewLifeCycle(): Lifecycle {
        return viewLifecycleOwner.lifecycle
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionLocationGranted()
                } else {
                    onPermissionLocationDenied()
                }
            }
            RC_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionStorageGranted()
                } else {
                    onPermissionStorageDenied()
                }
            }
            RC_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionCameraGranted()
                } else {
                    onPermissionCameraDenied()
                }
            }
        }
    }

    protected open fun onPermissionCameraDenied() {

    }

    protected open fun onPermissionCameraGranted() {

    }

    protected open fun onPermissionLocationGranted() {

    }

    protected open fun onPermissionLocationDenied() {

    }

    protected open fun onPermissionStorageGranted() {

    }

    protected open fun onPermissionStorageDenied() {

    }

    protected fun checkLocationPermissions(): Boolean {
        return baseActivity!!.checkLocationPermissions()
    }

    protected fun checkStoragePermissions(): Boolean {
        return baseActivity!!.checkStoragePermissions()
    }

    protected fun checkCameraPermission(): Boolean {
        return baseActivity!!.checkCameraPermission()
    }

    protected fun requestLocationPermissions() {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), RC_LOCATION_PERMISSION)
    }
    protected fun requestStoragePermissions() {
        requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), RC_READ_EXTERNAL_STORAGE_PERMISSION)
    }

    protected fun requestCameraPermission() {
        requestPermissions(arrayOf(CAMERA), RC_CAMERA_PERMISSION)
    }

    fun showLoading(type : LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        baseActivity?.showLoading(type)
    }

    fun hideLoading() {
        baseActivity?.hideLoading()
    }

    protected fun showError(message: String?) {
        baseActivity?.showError(message)
    }










    class ViewLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        override fun getLifecycle(): LifecycleRegistry {
            return lifecycleRegistry
        }
    }


}
