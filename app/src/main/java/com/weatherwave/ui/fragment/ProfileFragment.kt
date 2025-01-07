package com.weatherwave.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.weatherwave.R
import com.weatherwave.ui.activity.AboutActivity
import com.weatherwave.ui.activity.EditProfileActivity
import com.weatherwave.ui.activity.LoginActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileNameTextView: TextView
    private lateinit var profileShipOrRoleTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileNameTextView = view.findViewById(R.id.profileName)
        profileShipOrRoleTextView = view.findViewById(R.id.profileShip)

        updateProfile()

        // Edit Profil Button
        view.findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // Tentang Aplikasi Section
        view.findViewById<RelativeLayout>(R.id.aboutApp).setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        // Logout Section
        view.findViewById<RelativeLayout>(R.id.logoutSection).setOnClickListener {
            performLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()  // Ensure profile data is updated whenever the view resumes
    }

    private fun updateProfile() {
        val sharedPreferences = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userName = sharedPreferences?.getString("name", "Nama tidak tersedia")
        val kodeKapal = sharedPreferences?.getString("kode_kapal", "Kode Kapal tidak tersedia").orEmpty().toUpperCase()
        val roleId = sharedPreferences?.getInt("role_id", 2)  // Assuming 2 is the default user role

        profileNameTextView.text = userName
        profileShipOrRoleTextView.text = when (roleId) {
            1 -> "ADMIN"
            else -> kodeKapal
        }
    }

    private fun performLogout() {
        val sharedPreferences = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()

        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }
}
