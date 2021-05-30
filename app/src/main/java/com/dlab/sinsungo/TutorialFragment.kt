package com.dlab.sinsungo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dlab.sinsungo.databinding.FragmentTutorialBinding

class TutorialFragment(private val position: Int) : Fragment() {
    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)

        when (position) {
            0 -> {
                Glide.with(requireContext()).load(R.drawable.img_tutorial1).into(binding.ivTutorialImage)
                binding.tvTutorialTitle.text = getString(R.string.tutorial_title1)
                binding.tvTutorialContent.text = getString(R.string.tutorial_content1)
            }
            1 -> {
                Glide.with(requireContext()).load(R.drawable.img_tutorial2).into(binding.ivTutorialImage)
                binding.tvTutorialTitle.text = getString(R.string.tutorial_title2)
                binding.tvTutorialContent.text = getString(R.string.tutorial_content2)
            }
            2 -> {
                Glide.with(requireContext()).load(R.drawable.img_tutorial3).into(binding.ivTutorialImage)
                binding.tvTutorialTitle.text = getString(R.string.tutorial_title3)
                binding.tvTutorialContent.text = getString(R.string.tutorial_content3)
            }
            3 -> {
                Glide.with(requireContext()).load(R.drawable.img_welcome).into(binding.ivTutorialImage)
                binding.tvTutorialTitle.text = getString(R.string.tutorial_title_welcome)
                binding.tvTutorialContent.text = getString(R.string.tutorial_content_welcome)
            }
        }

        return binding.root
    }
}
