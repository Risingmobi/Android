package com.alex.tur.client.ui.profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.client.ui.address.search.AddressSearchActivity
import com.alex.tur.client.ui.login.AuthActivity
import com.alex.tur.client.ui.profile.edit.ClientEditActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.helper.Result
import com.alex.tur.model.Customer
import com.alex.tur.ui.profile.edit.EditActivity
import com.alex.tur.utils.FileUtils
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kotlinx.android.synthetic.client.fragment_profile.*
import kotlinx.android.synthetic.main.profile_item.view.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import cards.pay.paycardsrecognizer.sdk.utils.CardUtils.getCardNumberRedacted
import android.R.attr.data
import cards.pay.paycardsrecognizer.sdk.Card


class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ProfileViewModel>

    lateinit var viewModel: ProfileViewModel

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var tempPhotoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        swipeRefreshLayout = inflater.inflate(R.layout.fragment_profile, container, false) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestData(true)
        }
        return swipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailBtn.paramTextView.text = "Email"
        phoneBtn.paramTextView.text = "Phone number"
        addressBtn.paramTextView.text = "Address"
        paycardBtn.paramTextView.text = "Pay card"
        notificationsBtn.paramTextView.text = "Notifications"
        passwordBtn.paramTextView.text = "Change password"
        emailBtn.icon.setImageResource(R.drawable.ic_email_black_24dp)
        phoneBtn.icon.setImageResource(R.drawable.ic_local_phone_black_24dp)
        addressBtn.icon.setImageResource(R.drawable.ic_home_black_24dp)
        paycardBtn.icon.setImageResource(R.drawable.ic_payment_black_24dp)
        notificationsBtn.icon.setImageResource(R.drawable.ic_notifications_black_24dp)
        passwordBtn.icon.setImageResource(R.drawable.ic_lock_black_24dp)

        photoBtn.setOnClickListener {
            showAvatarSelectionDialog()
        }

        editNameBtn.setOnClickListener {
            viewModel.onEditNameClicked()
        }

        emailBtn.setOnClickListener {
            viewModel.onEditEmailClicked()
        }

        phoneBtn.setOnClickListener {
            viewModel.onEditPhoneClicked()
        }

        addressBtn.setOnClickListener {
            viewModel.onEditAddressClicked()
        }

        paycardBtn.setOnClickListener {
            viewModel.onEditPayCardClicked()
        }

        notificationsBtn.setOnClickListener {

        }

        passwordBtn.setOnClickListener {
            viewModel.onEditPasswordClicked()
        }

        logoutBtn.setOnClickListener {
            viewModel.onLogoutClicked()
        }
    }

    private fun showAvatarSelectionDialog() {
        AlertDialog.Builder(context!!)
                .setItems(arrayOf("Pick image", "Take photo"), { dialog, wich ->
                    when(wich) {
                        0 -> {
                            viewModel.onPickImageClicked()
                        }
                        1 -> {
                            viewModel.onTakePhotoClicked()
                        }
                    }
                })
                .show()
    }

    override fun onPermissionStorageGranted() {
        viewModel.onPickImageClicked()
    }

    override fun onPermissionCameraGranted() {
        viewModel.onTakePhotoClicked()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            RC_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.uploadAvatar(FileUtils.getPathFromUri(context, data?.data))
                }
            }
            RC_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.uploadAvatar(tempPhotoFile?.absolutePath)
                }
            }
            RC_CARD_SCAN -> {
                if (resultCode == Activity.RESULT_OK) {
                    val card = data?.getParcelableExtra(ScanCardIntent.RESULT_PAYCARDS_CARD) as Card?
                    val cardData = ("Card number: " + card?.cardNumberRedacted + "\n"
                            + "Card holder: " + card?.cardHolderName + "\n"
                            + "Card expiration date: " + card?.expirationDate)
                    Timber.d("Card info: $cardData")
                    /*var resultDisplayStr: String? = null
                    if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                        val scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT) as CreditCard

                        // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                        resultDisplayStr = "Card Number: " + scanResult.redactedCardNumber + "\n"

                        // Do something with the raw number, e.g.:
                        // myService.setCardNumber( scanResult.cardNumber );

                        if (scanResult.isExpiryValid) {
                            resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n"
                        }

                        if (scanResult.cvv != null) {
                            // Never log or display a CVV
                            resultDisplayStr += "CVV has " + scanResult.cvv.length + " digits.\n"
                        }

                        if (scanResult.postalCode != null) {
                            resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n"
                        }
                    }
                    Timber.d("scan res $resultDisplayStr")*/
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel(ProfileViewModel::class, viewModelFactory)

        viewModel.profileResult.observe(viewLifecycleOwner, Observer {
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
        })
        viewModel.editNameAction.observe(viewLifecycleOwner, Observer {
            ClientEditActivity.start(activity, EditActivity.EDIT_TYPE_NAME, it)
        })
        viewModel.editEmailAction.observe(viewLifecycleOwner, Observer {
            ClientEditActivity.start(activity, EditActivity.EDIT_TYPE_EMAIL, it)
        })
        viewModel.editPasswordAction.observe(viewLifecycleOwner, Observer {
            ClientEditActivity.start(activity, EditActivity.EDIT_TYPE_PASSWORD, it)
        })
        viewModel.editAddressAction.observe(viewLifecycleOwner, Observer {
            AddressSearchActivity.start(this, it)
        })
        viewModel.logoutAction.observe(viewLifecycleOwner, Observer {
            onLoggedOut()
        })
        viewModel.changeAvatarError.observe(viewLifecycleOwner, Observer {
            showError(it)
        })
        viewModel.editPhoneAction.observe(viewLifecycleOwner, Observer {
            ClientEditActivity.start(activity, EditActivity.EDIT_TYPE_PHONE, it)
        })
        viewModel.editCardAction.observe(viewLifecycleOwner, Observer {
//            val scanIntent = Intent(activity, CardIOActivity::class.java)
//
//            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
//            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false
//            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false
//
//            startActivityForResult(scanIntent, RC_CARD_SCAN)

            val intent = ScanCardIntent.Builder(activity).build()
            startActivityForResult(intent, RC_CARD_SCAN)
        })
        viewModel.pickImageClickHandler.observe(viewLifecycleOwner, Observer {
            if (checkStoragePermissions()) {
                startActivityForResult(Intent.createChooser(Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }, "Select Picture"), RC_PICK_IMAGE)
            } else {
                requestStoragePermissions()
            }
        })

        viewModel.takePhotoClickHandler.observe(viewLifecycleOwner, Observer {
            if (checkCameraPermission()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
                    tempPhotoFile = FileUtils.createImageFile(context)
                    val temp = tempPhotoFile

                    if (temp != null) {
                        val photoURI = FileProvider.getUriForFile(context!!,
                                getString(R.string.file_provider_name),
                                temp)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE)
                    }
                }
            } else {
                requestCameraPermission()
            }
        })
        viewModel.requestData()
    }

    private fun attachProfileData(customer: Customer?) {
        customer?.let {
            nameTextView.text = it.name
            emailBtn.valueTextView.text = it.email
            phoneBtn.valueTextView.text = it.phone
            addressBtn.valueTextView.text = it.addressString
            avatarView.setAvatar(it.avatar)
        }
    }

    private fun onLoggedOut() {
        activity?.finishAffinity()
        AuthActivity.start(activity)
    }

    companion object {
        const val TAG = "ProfileFragment"
        const val RC_PICK_IMAGE = 1
        const val RC_IMAGE_CAPTURE = 2
        const val RC_CARD_SCAN = 3
    }
}