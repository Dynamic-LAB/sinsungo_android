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
            0 -> IngredientFragment("냉장")
            1 -> IngredientFragment("냉동")
            2 -> IngredientFragment("신선")
            3 -> IngredientFragment("상온")
            else -> IngredientFragment("조미료/양념")
        }
    }
}
