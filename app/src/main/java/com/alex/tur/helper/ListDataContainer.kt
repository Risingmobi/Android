package com.alex.tur.helper

import android.content.Context
import android.graphics.Color
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.Guideline
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.isEmpty
import androidx.core.view.setMargins
import com.alex.tur.R
import com.alex.tur.utils.DimensUtils
import timber.log.Timber

class ListDataContainer @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var onRefreshListener: (() -> Unit)? = null

    val errorView = ActionView(context)
    val emptyView = ActionView(context)
    private val loadingView = SwipeRefreshLayout(context)
    private var content: View? = null

    init {
        errorView.id = View.generateViewId()
        emptyView.id = View.generateViewId()
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
        emptyView.button.visibility = View.GONE
        addView(loadingView)
        addView(errorView)
        addView(emptyView)


        emptyView.textView.text = context.getString(R.string.no_content)
        emptyView.imageView.visibility = View.GONE
        errorView.textView.text = context.getString(R.string.some_error)
        errorView.imageView.visibility = View.GONE
        errorView.button.text = context.getString(R.string.retry)
        errorView.button.setOnClickListener {
            Timber.d("click")
            onRefreshListener?.invoke()
        }
        loadingView.setOnRefreshListener {
            onRefreshListener?.invoke()
        }
    }

    fun attachContentView(view: View) {
        if (content == null) {
            content = view
            loadingView.addView(content)
        }
    }

    fun setOnRefreshListener(onRefreshListener: () -> Unit) {
        this.onRefreshListener = onRefreshListener
    }

    fun <T>setResult(result: Result<T>?, isEmpty: (T?) -> Boolean) {
        Timber.d("setResult %s, %s, error: %s, hasData: %s", result?.status, result?.type, result?.message, (result?.data != null))
//        Timber.d("setResult %s", result)
        when(result?.status) {
            Result.Status.SUCCESS -> {
                when(result.type) {
                    Result.Type.LOCAL -> {
                        if(isEmpty(result.data)) {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = GONE
                            loadingView.isRefreshing = true
                        } else {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = View.VISIBLE
                            loadingView.isRefreshing = false
                        }
                    }
                    Result.Type.REMOTE -> {
                        if(isEmpty(result.data)) {
                            errorView.visibility = GONE
                            emptyView.visibility = View.VISIBLE
                            content?.visibility = GONE
                            loadingView.isRefreshing = false
                        } else {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = View.VISIBLE
                            loadingView.isRefreshing = false
                        }
                    }
                }
            }
            Result.Status.ERROR -> {
                when(result.type) {
                    Result.Type.LOCAL -> {
                        if(isEmpty(result.data)) {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = GONE
                            loadingView.isRefreshing = true
                        } else {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = View.VISIBLE
                            loadingView.isRefreshing = true
                        }
                    }
                    Result.Type.REMOTE -> {
                        if(isEmpty(result.data)) {
                            errorView.visibility = View.VISIBLE
                            emptyView.visibility = GONE
                            content?.visibility = GONE
                            loadingView.isRefreshing = false

                            errorView.textView.text = result.message
                        } else {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = View.VISIBLE
                            loadingView.isRefreshing = false

                            showError(result.message)
                        }
                    }
                }
            }
            Result.Status.LOADING -> {
                if(isEmpty(result.data)) {
                    when {
                        result.isInitial -> {
                            errorView.visibility = GONE
                            emptyView.visibility = GONE
                            content?.visibility = GONE
                            loadingView.isRefreshing = true
                        }
                        result.hasError -> {
                            errorView.visibility = View.VISIBLE
                            emptyView.visibility = GONE
                            content?.visibility = GONE
                            loadingView.isRefreshing = true

                            errorView.textView.text = result.message
                        }
                        else -> {
                            errorView.visibility = GONE
                            emptyView.visibility = View.VISIBLE
                            content?.visibility = GONE
                            loadingView.isRefreshing = true
                        }
                    }
                } else {
                    errorView.visibility = GONE
                    emptyView.visibility = GONE
                    content?.visibility = View.VISIBLE
                    loadingView.isRefreshing = true
                }
            }
            null -> {
                applyNone()
            }
        }
    }

    fun showError(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun applyNone() {
        errorView.visibility = GONE
        emptyView.visibility = GONE
        content?.visibility = GONE
        loadingView.isRefreshing = false
    }

    fun setEmptyImageResource(@DrawableRes res: Int) {
        if (res == 0) {
            emptyView.imageView.visibility = View.GONE
            GlideHelper.clear(context, emptyView.imageView)
        } else {
            emptyView.imageView.visibility = View.VISIBLE
            GlideHelper.load(context, emptyView.imageView, res)
        }
    }

    fun setEmptyMessageResource(@StringRes res: Int) {
        emptyView.textView.text = context.getString(res)
    }


    class ActionView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr) {

        val imageView: ImageView
        val textView: TextView
        val button: Button

        init {
            val content = LayoutInflater.from(context).inflate(R.layout.action_view, this)
            imageView = content.findViewById(R.id.image)
            textView = content.findViewById(R.id.text)
            button = content.findViewById(R.id.button)
        }
    }
}