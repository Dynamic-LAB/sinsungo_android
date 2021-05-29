package com.dlab.sinsungo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.FragmentTutorialBinding

class TutorialFragment(private val position: Int) : Fragment() {
    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)

        return binding.root
    }
}
