package com.weatherwave.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextNama = findViewById<EditText>(R.id.editTextNama)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextTelepon = findViewById<EditText>(R.id.editTextTelepon)
        val editTextDomisili = findViewById<EditText>(R.id.editTextDomisili)
        val editTextKodeKapal = findViewById<EditText>(R.id.editTextKodeKapal)
        val textViewKodeKapal = findViewById<TextView>(R.id.textViewKodeKapal)

        editTextNama.setText(sharedPreferences.getString("name", ""))
        editTextEmail.setText(sharedPreferences.getString("email", ""))
        editTextPassword.setText("")
        editTextTelepon.setText(sharedPreferences.getString("no_telp", ""))
        editTextDomisili.setText(sharedPreferences.getString("domisili", ""))
        editTextKodeKapal.setText(sharedPreferences.getString("kode_kapal", ""))

        val roleId = sharedPreferences.getInt("role_id", 2)
        if (roleId == 1) {
            editTextKodeKapal.visibility = View.GONE
            textViewKodeKapal.visibility = View.GONE
        }

        val saveButton = findViewById<Button>(R.id.btnLogin)
        saveButton.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun saveProfileChanges() {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getInt("user_id", 0)
        val nama = findViewById<EditText>(R.id.editTextNama).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val kodeKapal = if (findViewById<EditText>(R.id.editTextKodeKapal).visibility == View.VISIBLE)
            findViewById<EditText>(R.id.editTextKodeKapal).text.toString() else ""
        val domisili = findViewById<EditText>(R.id.editTextDomisili).text.toString()
        val noTelp = findViewById<EditText>(R.id.editTextTelepon).text.toString()
        val roleId = sharedPreferences.getInt("role_id", 0)

        val user = Users(userId, nama, email, password, kodeKapal, domisili, noTelp, roleId)

        RetrofitClient.instance.updateUser(user).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let {
                    if (it.status) {
                        val editor = sharedPreferences.edit()
                        editor.putString("name", user.nama)
                        editor.putString("email", user.email)
                        editor.putString("no_telp", user.no_telp)
                        editor.putString("domisili", user.domisili)
                        editor.putString("kode_kapal", user.kode_kapal)
                        editor.apply()

                        Toast.makeText(this@EditProfileActivity, it.message, Toast.LENGTH_LONG).show()

                        val intent = if (roleId == 1) {
                            Intent(this@EditProfileActivity, AdminActivity::class.java)
                        } else {
                            Intent(this@EditProfileActivity, UserActivity::class.java)
                        }
                        intent.putExtra("FRAGMENT_ID", R.id.profile)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
