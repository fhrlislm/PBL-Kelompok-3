package com.weatherwave.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.weatherwave.R

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        // Delay selama 3 detik sebelum berpindah
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000) // 3000 ms = 3 detik
    }

    private fun navigateToNextScreen() {
        // Cek apakah onboarding sudah ditampilkan
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        } else {
            checkLoginAndNavigate()
        }

        finish() // Menutup SplashActivity
    }

    private fun checkLoginAndNavigate() {
        // Cek apakah user sudah login berdasarkan 'username'
        val username = sharedPreferences.getString("username", null)
        val roleId = sharedPreferences.getInt("role_id", 0) // 1 = Admin, 2 = User

        val intent = if (username == null) {
            // Jika belum login, arahkan ke LoginActivity
            Intent(this, LoginActivity::class.java)
        } else {
            // Jika sudah login, tentukan activity berdasarkan role
            when (roleId) {
                1 -> Intent(this, AdminActivity::class.java) // Role Admin
                2 -> Intent(this, UserActivity::class.java)  // Role User
                else -> Intent(this, LoginActivity::class.java) // Default ke Login jika role tidak dikenali
            }
        }

        startActivity(intent)
    }
}
