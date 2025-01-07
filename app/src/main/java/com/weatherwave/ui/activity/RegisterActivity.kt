package com.weatherwave.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Sinkronisasi ID dengan XML
        val namaEditText = findViewById<EditText>(R.id.etFullName)
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val domisiliEditText = findViewById<EditText>(R.id.etDomisili)
        val notelpEditText = findViewById<EditText>(R.id.etNotelp)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // Tombol untuk pindah ke LoginActivity
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Fungsi untuk tombol registrasi
        registerButton.setOnClickListener {
            val nama = namaEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val domisili = domisiliEditText.text.toString()
            val no_telp = notelpEditText.text.toString()

            if (nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && domisili.isNotEmpty() && no_telp.isNotEmpty()) {
                // Tambahkan 0 sebagai user_id default
                val user = Users(0, nama, email, password, "", domisili, no_telp, 0)
                registerUser(user)
            } else {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(user: Users) {
        // Log data yang akan dikirim
        Log.d("RegisterActivity", "Register data: ${user}")

        val call = RetrofitClient.instance.registerUser(user)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Log respons dari server
                Log.d("RegisterActivity", "Server Response: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()
                    if (apiResponse!!.status) {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${apiResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("RegisterActivity", "API Call Failure: ${t.message}", t)
                Toast.makeText(this@RegisterActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
