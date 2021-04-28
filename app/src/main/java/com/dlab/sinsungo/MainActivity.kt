package com.dlab.sinsungo

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sinsungo)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.setOnNavigationItemSelectedListener(this)
        changeBottomNavMenu(R.id.bottom_nav_menu_refrigerator)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.tvFragmentTitle.text = item.title.toString()
        when (item.itemId) {
            R.id.bottom_nav_menu_refrigerator -> {
                val fragment = RefrigeratorFragment()
                changeFragment(fragment)
            }
            R.id.bottom_nav_menu_diet -> {
                val fragment = DietFragment()
                changeFragment(fragment)
            }
            R.id.bottom_nav_menu_recipe -> {
                // changeFragment()
            }
            R.id.bottom_nav_menu_notification -> {
                // changeFragment()
            }
            R.id.bottom_nav_menu_shopping_basket -> {
                val fragment = ShoppingFragment()
                changeFragment(fragment)
            }
        }
        return true
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, fragment)
            .commit()
    }

    public fun changeBottomNavMenu(menuID: Int) {
        binding.bottomNavView.selectedItemId = menuID
    }
}
