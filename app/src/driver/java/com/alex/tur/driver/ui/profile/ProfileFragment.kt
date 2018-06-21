package com.alex.tur.driver.ui.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.driver.ui.login.AuthActivity
import com.alex.tur.driver.ui.profile.edit.DriverEditActivity
import com.alex.tur.driver.ui.profile.edit_car.EditCarActivity
import com.alex.tur.driver.ui.servicelist.DriverServicesActivity
import com.alex.tur.ext.fetchServicesNames
import com.alex.tur.helper.GlideHelper
import com.alex.tur.helper.Result
import com.alex.tur.model.Driver
import com.alex.tur.model.DriverStatus
import com.alex.tur.ui.profile.edit.EditActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.driver.fragment_profile.*
import kotlinx.android.synthetic.main.profile_item.view.*
import kotlinx.android.synthetic.main.profile_switch_item.view.*
import timber.log.Timber
import javax.inject.Inject

class ProfileFragment: BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ProfileViewModel>

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var viewModel: ProfileViewModel

    private val editPasswordActionObserver = Observer<String?> {
        DriverEditActivity.start(activity, EditActivity.EDIT_TYPE_PASSWORD, it)
    }
    private val editCarActionObserver = Observer<EditCarActivity.CarInfo> {
        it?.also {
            EditCarActivity.start(activity, it)
        }
    }
    private val logoutActionObserver = Observer<Unit> {
        onLoggedOut()
    }
    private val profileObserver = Observer<Result<Driver>> {
        it?.let {
            when(it.status) {
                Result.Status.SUCCESS -> {
                    swipeRefreshLayout.isRefreshing = false
                    attachProfileData(it.data)
                }
                Result.Status.LOADING -> {
                    attachProfileData(it.data)
                    swipeRefreshLayout.isRefreshing = true
                }
                Result.Status.ERROR -> {
                    attachProfileData(it.data)
                    swipeRefreshLayout.isRefreshing = false
                    showError(it.message)
                }
            }
        }
    }
    private val statusObserver = Observer<Result<DriverStatus>> {
        avatarView.setStatus(it?.data)
        availableBtn.switchView_sw.setOnCheckedChangeListener(null)
        availableBtn.switchView_sw.isChecked = it?.data == DriverStatus.ACTIVE
        availableBtn.switchView_sw.setOnCheckedChangeListener({ _, isChecked ->
            viewModel.setAvailable(isChecked)
        })
        when(it?.data) {
            DriverStatus.ACTIVE, DriverStatus.PENDING -> {availableBtn.icon_sw.setImageResource(R.drawable.ic_visibility_black_24dp)}
            DriverStatus.INACTIVE, null -> {availableBtn.icon_sw.setImageResource(R.drawable.ic_visibility_off_black_24dp)}
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        swipeRefreshLayout = inflater.inflate(R.layout.fragment_profile, container, false) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestData(true)
        }
        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        servicesBtn.paramTextView.text = "Services Offered"
        carBtn.paramTextView.text = ""
        passwordBtn.paramTextView.text = "Change Password"
        availableBtn.paramTextView_sw.text = "Availability"
        emailBtn.paramTextView.text = "Email"
        phoneBtn.paramTextView.text = "Phone Number"

        servicesBtn.icon.setImageResource(R.drawable.ic_request_service)
        carBtn.icon.setImageResource(R.drawable.ic_directions_car_black_24dp)
        passwordBtn.icon.setImageResource(R.drawable.ic_lock_black_24dp)
        availableBtn.icon_sw.setImageResource(R.drawable.ic_visibility_off_black_24dp)
        emailBtn.icon.setImageResource(R.drawable.ic_email_black_24dp)
        phoneBtn.icon.setImageResource(R.drawable.ic_local_phone_black_24dp)

        Timber.d("view %s", availableBtn.icon_sw)

        emailBtn.arrowImageView.visibility = View.GONE
        phoneBtn.arrowImageView.visibility = View.GONE

        servicesBtn.setOnClickListener {
            DriverServicesActivity.start(activity)
        }

        passwordBtn.setOnClickListener {
            viewModel.onEditPasswordClicked()
        }

        logoutBtn.setOnClickListener {
            viewModel.onLogOutClicked()
        }

        carBtn.setOnClickListener {
            viewModel.onEditCarClicked()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        lifecycle.addObserver(viewModel)
        viewModel.editPasswordAction.observe(viewLifecycleOwner, editPasswordActionObserver)
        viewModel.editCarAction.observe(viewLifecycleOwner, editCarActionObserver)
        viewModel.profile.observe(viewLifecycleOwner, profileObserver)
        viewModel.logoutAction.observe(viewLifecycleOwner, logoutActionObserver)
        viewModel.status.observe(viewLifecycleOwner, statusObserver)
        viewModel.requestData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.editPasswordAction.removeObserver(editPasswordActionObserver)
        viewModel.editCarAction.removeObserver(editCarActionObserver)
        viewModel.profile.removeObserver(profileObserver)
        viewModel.logoutAction.removeObserver(logoutActionObserver)
        viewModel.status.removeObserver(statusObserver)
    }

    private fun attachProfileData(driver: Driver?) {
        driver?.let {
            nameTextView.text = it.name
            emailBtn.valueTextView.text = it.email
            phoneBtn.valueTextView.text = it.phone
            avatarView.setAvatar(it.avatar)
            carBtn.paramTextView.text = it.vehicleModel
            carBtn.valueTextView.text = it.vehicleNumber
            servicesBtn.valueTextView.text = it.fetchServicesNames()
            GlideHelper.load(context, companyImageView, it.company?.picture)
            companyTextView.text = it.company?.naming
        }
    }

    private fun onLoggedOut() {
        activity?.finishAffinity()
        AuthActivity.start(activity)
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}
