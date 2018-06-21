package com.alex.tur.client.ui.comment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.model.OrderDescription
import kotlinx.android.synthetic.client.activity_comment.*
import kotlinx.android.synthetic.client.fragment_comment_text.*
import javax.inject.Inject

class CommentActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<CommentViewModel>

    lateinit var viewModel: CommentViewModel

    @Inject
    lateinit var action: String

    companion object {
        const val EXTRA_COMMENT = "EXTRA_COMMENT"

        const val ACTION_CREATE = "ACTION_CREATE"
        const val ACTION_EDIT = "ACTION_EDIT"

        fun startForResult(fragment: Fragment, comment: OrderDescription?, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context, CommentActivity::class.java).apply {
                putExtra(EXTRA_COMMENT, comment)
                action = CommentActivity.ACTION_CREATE
            }, requestCode)
        }

        fun start(fragment: Fragment, comment: OrderDescription) {
            fragment.startActivity(Intent(fragment.context, CommentActivity::class.java).apply {
                putExtra(EXTRA_COMMENT, comment)
                action = CommentActivity.ACTION_EDIT
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = Adapter(supportFragmentManager)

        doneBtn.setOnClickListener {
            viewModel.onDoneClicked()
        }

        viewModel = getViewModel(CommentViewModel::class, viewModelFactory)

        viewModel.doneClickHandler.observe(this, Observer {
            it?.also {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(EXTRA_COMMENT, it)
                })
                finish()
            }
        })
    }



    inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> {
                    CommentTextFragment()
                }
                1 -> {
                    CommentImageFragment()
                }
                else -> {
                    throw IllegalStateException("Can not find fragment for position $position")
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> {
                    getString(R.string.comment)
                }
                1 -> {
                    getString(R.string.image)
                }
                else -> {
                    throw IllegalStateException("Can not find title for position $position")
                }
            }
        }
    }
}