package com.alex.tur.client.ui.comment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.tur.R
import com.alex.tur.base.BaseFragment
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.helper.GlideHelper
import com.alex.tur.utils.FileUtils
import kotlinx.android.synthetic.client.fragment_comment_image.*
import java.io.File
import javax.inject.Inject

class CommentImageFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CommentViewModel>

    lateinit var viewModel: CommentViewModel

    private var tempPhotoFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comment_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity!!.getViewModel(CommentViewModel::class, viewModelFactory)

        viewModel.comment.observe(viewLifecycleOwner, Observer {
            GlideHelper.load(context, imageView, it?.picture)
            if (it?.picture == null) {
                addImageBtn.visibility = View.VISIBLE
            } else {
                addImageBtn.visibility = View.GONE
            }
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
                                "com.client.android.fileprovider", //Same as in manifest
                                temp)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE)
                    }
                }
            } else {
                requestCameraPermission()
            }
        })

        addImageBtn.setOnClickListener {
            showDialog(false)
        }

        imageView.setOnClickListener {
            showDialog(true)
        }
    }

    private fun showDialog(withRemove: Boolean) {
        val actions = if (withRemove) {
            arrayOf("Pick image", "Take photo", "Remove image")
        } else {
            arrayOf("Pick image", "Take photo")
        }
        AlertDialog.Builder(context!!)
                .setItems(actions, { dialog, wich ->
                    when(wich) {
                        0 -> {
                            viewModel.onPickImageClicked()
                        }
                        1 -> {
                            viewModel.onTakePhotoClicked()
                        }
                        2 -> {
                            viewModel.onRemoveImageClicked()
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
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RC_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.onImageSelected(FileUtils.getPathFromUri(context, data?.data))
                }
            }
            RC_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.onPhotoSelected(tempPhotoFile?.absolutePath)
                }
            }
        }
    }

    companion object {
        private const val RC_PICK_IMAGE = 0
        private const val RC_IMAGE_CAPTURE = 1
    }
}
