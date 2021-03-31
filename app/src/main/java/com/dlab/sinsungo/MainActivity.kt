package com.dlab.sinsungo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
        binding.bottomNavView.selectedItemId = R.id.bottom_nav_menu_refrigerator
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.bottom_nav_menu_diet ->{
                // changeFragement()
            }
            R.id.bottom_nav_menu_recipe ->{
                // changeFragment()
            }
            R.id.bottom_nav_menu_refrigerator ->{
                // changeFragment()
            }
            R.id.bottom_nav_menu_notification ->{
                // changeFragment()
            }
            R.id.bottom_nav_menu_shopping_basket ->{
                // changeFragment()
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
}
