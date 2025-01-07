package com.weatherwave.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.weatherwave.R
import com.weatherwave.model.ApiResponse
import com.weatherwave.model.Users
import com.weatherwave.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val tvDaftar = findViewById<TextView>(R.id.tvRegister)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        val username = sharedPreferences.getString("name", null)
        if (username != null) {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity
        }

        // Go to RegisterActivity
        tvDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = Users(nama = "", email = email, password = password)
                loginUser(user)
            } else {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(user: Users) {
        val call = RetrofitClient.instance.loginUser(user)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status) {
                        val editor = sharedPreferences.edit()
                        editor.putString("name", apiResponse.name) // Save user name
                        editor.putInt("role_id", apiResponse.role_id ?: 0) // Save role ID
                        editor.putInt("user_id", apiResponse.user_id ?: -1) // Save user ID
                        editor.putString("email", apiResponse.email)
                        editor.putString("password", apiResponse.password)
                        editor.putString("kode_kapal", apiResponse.kode_kapal) // Save kode_kapal
                        editor.putString("domisili", apiResponse.domisili) // Save domisili
                        editor.putString("no_telp", apiResponse.no_telp) // Save no_telp
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                        // Redirect based on role_id
                        val intent = when (apiResponse.role_id) {
                            1 -> { // Admin
                                val adminIntent = Intent(this@LoginActivity, AdminActivity::class.java)
                                adminIntent.putExtra("name", apiResponse.name) // Include username
                                adminIntent
                            }
                            2 -> { // User
                                val userIntent = Intent(this@LoginActivity, UserActivity::class.java)
                                userIntent.putExtra("name", apiResponse.name) // Include username
                                userIntent
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "Role tidak dikenali", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, apiResponse?.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}