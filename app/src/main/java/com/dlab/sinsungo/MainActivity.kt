package com.dlab.sinsungo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.ActivityMainBinding
import com.dlab.sinsungo.ui.MyPageActivity
import com.dlab.sinsungo.ui.RecipeFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val ingredientViewModel: IngredientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sinsungo)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.view = this
        binding.bottomNavView.setOnNavigationItemSelectedListener(this)
        changeBottomNavMenu(R.id.bottom_nav_menu_refrigerator)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FCM", token!!)
            Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
        })
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
                val fragment = RecipeFragment()
                changeFragment(fragment)
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

    fun changeBottomNavMenu(menuID: Int) {
        binding.bottomNavView.selectedItemId = menuID
    }


    fun startMyPage() {
        val intent = Intent(this, MyPageActivity::class.java)
        startActivity(intent)
    }
}
