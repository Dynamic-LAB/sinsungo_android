package com.dlab.sinsungo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.FragmentShoppingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class ShoppingFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentShoppingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShoppingBinding.inflate(inflater, container, false)
        initSpeedDialItem()
        binding.sdvShopping.setOnActionSelectedListener(this)
        return binding.root
    }

    private fun initSpeedDialItem() {
        binding.sdvShopping.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_shopping, R.drawable.btn_edit)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.shop_add)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 0
        )

        binding.sdvShopping.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_share_shopping, R.drawable.btn_share)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.shop_share)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 1
        )
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean{
        when (actionItem?.id){
            R.id.fab_share_shopping -> {
                //TODO
                binding.sdvShopping.close()
            }
            R.id.fab_add_shopping -> {
                binding.sdvShopping.close()
            }
        }
        return true
    }
}
