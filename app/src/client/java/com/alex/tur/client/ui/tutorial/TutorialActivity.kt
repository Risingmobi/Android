package com.alex.tur.client.ui.tutorial

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.alex.tur.R
import com.alex.tur.client.ui.main.MainActivity
import kotlinx.android.synthetic.client.activity_tutorial.*
import timber.log.Timber

class TutorialActivity : AppCompatActivity() {

    private val list = mutableListOf<Tut>()

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, TutorialActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        list.add(Tut(R.drawable.tutorial_1, "Choose the address", "Specify the address where to send the service."))
        list.add(Tut(R.drawable.tutorial_2, "Choose the Service", "Rapid service due to the fact that service is sent to the nearest performers."))
        list.add(Tut(R.drawable.tutorial_3, "WideRang of Services", "Simplify your life in just a couple of clicks."))

        setSkip()

        skipBtn.setOnClickListener {
            done()
        }

        doneBtn.setOnClickListener {
            done()
        }

        nextBtn.setOnClickListener {
            viewPager.currentItem++
        }

        viewPager.adapter = Adapter(supportFragmentManager)
        pageIndicatorView.setViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Timber.d("position %s, last %s", position, list.lastIndex)
                when (position) {
                    0 -> {
                        setSkip()
                    }
                    list.lastIndex -> {
                        setDone()
                    }
                    else -> {
                        setSkip()
                    }
                }
            }
        })
    }


    private fun setSkip() {
        Timber.d("setSkip")
        skipBtn.visibility = View.VISIBLE
        doneBtn.visibility = View.INVISIBLE
        nextBtn.visibility = View.VISIBLE

        skipBtn.isEnabled = true
        doneBtn.isEnabled = false
        nextBtn.isEnabled = true
    }

    private fun setDone() {
        Timber.d("setDone")
        skipBtn.visibility = View.INVISIBLE
        doneBtn.visibility = View.VISIBLE
        nextBtn.visibility = View.INVISIBLE

        skipBtn.isEnabled = false
        doneBtn.isEnabled = true
        nextBtn.isEnabled = false
    }

    private fun done() {
        skipBtn.isEnabled = false
        doneBtn.isEnabled = false
        nextBtn.isEnabled = false
        MainActivity.start(this)
        finish()
    }


    inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return TutorialFragment.newInstance(list[position])
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}
