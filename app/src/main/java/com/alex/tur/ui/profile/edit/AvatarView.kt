package com.alex.tur.ui.profile.edit

import android.content.Context
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.alex.tur.R
import com.alex.tur.helper.GlideHelper
import com.alex.tur.model.DriverStatus
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

class AvatarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val content = LayoutInflater.from(context).inflate(R.layout.avatar_view, this, true)
    private val avatarImageView: CircleImageView
    private val availableImageView: ImageView

    private var avatarUrl: String? = null

    init {
        avatarImageView = content.findViewById(R.id.profileImageView)
        availableImageView = content.findViewById(R.id.availabilityIcon)
    }

    fun setAvatar(avatar: String?) {
        Timber.d("avatar %s", avatar)
        avatarUrl = avatar
        avatar?.let {
            Timber.d("setAvatar %s", avatar)
            GlideHelper.loadAvatar(context, avatarImageView, avatar)
        }
    }

    fun setStatus(status: DriverStatus?) {
        when(status) {
            DriverStatus.ACTIVE -> {
                avatarImageView.borderColor = ContextCompat.getColor(context, R.color.colorAccent)
                availableImageView.visibility = View.VISIBLE
                availableImageView.isEnabled = true
            }
            DriverStatus.PENDING -> {
                availableImageView.visibility = View.GONE
                avatarImageView.borderColor = ContextCompat.getColor(context, R.color.colorDisabled)
            }
            DriverStatus.INACTIVE -> {
                availableImageView.visibility = View.VISIBLE
                availableImageView.isEnabled = false
                avatarImageView.borderColor = ContextCompat.getColor(context, R.color.colorDisabled)
            }
            null -> {
                availableImageView.visibility = View.GONE
                avatarImageView.borderColor = ContextCompat.getColor(context, R.color.colorDisabled)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        Timber.d("onSaveInstanceState")
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        Timber.d("onRestoreInstanceState")
    }
}