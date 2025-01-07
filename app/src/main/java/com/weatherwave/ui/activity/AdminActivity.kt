package com.weatherwave.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherwave.R
import com.weatherwave.ui.fragment.ProfileFragment
import com.weatherwave.ui.fragment.AdminFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var bottomNavAdmin: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        bottomNavAdmin = findViewById(R.id.bottomNavAdmin)

        val fragmentId = intent.getIntExtra("FRAGMENT_ID", -1)
        if (fragmentId != -1) {
            bottomNavAdmin.selectedItemId = fragmentId
        } else if (savedInstanceState == null) {
            loadFragment(AdminFragment())  // Default Fragment
        }

        bottomNavAdmin.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    loadFragment(AdminFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Function to load a fragment into the container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
