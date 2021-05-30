package com.dlab.sinsungo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dlab.sinsungo.databinding.ActivityTutorialBinding

class TutorialActivity : FragmentActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()

        binding.tvSkip.setOnClickListener {
            binding.pagerTutorial.setCurrentItem(3, true)
        }

        binding.btnInviteStart.setOnClickListener {

        }

        binding.btnNormalStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initViewPager() {
        binding.pagerTutorial.apply {
            adapter = TutorialPagerAdapter(this@TutorialActivity)
            offscreenPageLimit = 3
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 3) {
                        binding.tvSkip.visibility = View.GONE
                        binding.clTutorialButtons.visibility = View.VISIBLE
                    } else {
                        binding.tvSkip.visibility = View.VISIBLE
                        binding.clTutorialButtons.visibility = View.GONE
                    }
                }
            })
            currentItem = if (intent.getBooleanExtra("isFromSplash", true)) {
                3
            } else {
                0
            }
        }
        binding.tutorialIndicator.setViewPager2(binding.pagerTutorial)
    }

    inner class TutorialPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment = TutorialFragment(position)
    }
}
