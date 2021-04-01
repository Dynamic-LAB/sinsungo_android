package com.dlab.sinsungo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CustomFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> IngredientFragment()
            1 -> IngredientFragment()
            2 -> IngredientFragment()
            3 -> IngredientFragment()
            else -> IngredientFragment()
        }
    }
}
