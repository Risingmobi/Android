package com.alex.tur.ui.profile.edit

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import kotlinx.android.synthetic.main.activity_edit.*

abstract class EditActivity : BaseActivity() {

    companion object {
        const val EXTRA_EDIT_TYPE = "EXTRA_EDIT_TYPE"
        const val EXTRA_EDIT_PARAM = "EXTRA_EDIT_PARAM"

        const val EDIT_TYPE_NAME = "EDIT_TYPE_NAME"
        const val EDIT_TYPE_EMAIL = "EDIT_TYPE_EMAIL"
        const val EDIT_TYPE_PASSWORD = "EDIT_TYPE_PASSWORD"
        const val EDIT_TYPE_PHONE = "EDIT_TYPE_PHONE"
    }

    abstract fun getEditType(): String?
    abstract fun getEditParam(): String?
    abstract fun onEditNameClicked(name: String)
    abstract fun onEditEmailClicked(email: String)
    abstract fun onEditPasswordClicked(password: String)
    abstract fun onEditPhoneClicked(phone: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getEditType() == null) {
            finish()
            return
        }
        setContentView(R.layout.activity_edit)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        when(getEditType()) {
            EDIT_TYPE_NAME -> {
                toolbar.title = "Edit name"
                textInputLayout.hint = "First & last name"
                editText.inputType = InputType.TYPE_CLASS_TEXT
                confirmBtn.text = "Change first & last name"
                explainTextView.visibility = View.GONE
                explainTextView.text = ""
            }
            EDIT_TYPE_EMAIL -> {
                toolbar.title = "Change email"
                textInputLayout.hint = "Enter a new email"
                editText.inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                confirmBtn.text = "Change email"
                explainTextView.visibility = View.GONE
                explainTextView.text = ""
            }
            EDIT_TYPE_PASSWORD -> {
                toolbar.title = "Change password"
                textInputLayout.hint = "Enter a new password"
                editText.inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
                confirmBtn.text = "Change password"
                explainTextView.visibility = View.VISIBLE
                explainTextView.text = "A strong password consists of at least 5 signs and contains characters and numbers."
            }
            EDIT_TYPE_PHONE -> {
                toolbar.title = "Change phone"
                textInputLayout.hint = "Enter a new phone"
                editText.inputType = EditorInfo.TYPE_CLASS_PHONE
                confirmBtn.text = "Change phone"
                explainTextView.visibility = View.GONE
                explainTextView.text = ""
            }
        }

        confirmBtn.setOnClickListener({
            when(getEditType()) {
                EDIT_TYPE_NAME -> {
                    onEditNameClicked(editText.text.toString())
                }
                EDIT_TYPE_EMAIL -> {
                    onEditEmailClicked(editText.text.toString())
                }
                EDIT_TYPE_PASSWORD -> {
                    onEditPasswordClicked(editText.text.toString())
                }
                EDIT_TYPE_PHONE -> {
                    onEditPhoneClicked(editText.text.toString())
                }
            }
        })

        if (savedInstanceState == null) {
            editText.setText(getEditParam())
        }

        editText.selectAll()

        clearBtn.setOnClickListener {
            editText.setText("")
        }
    }


}
