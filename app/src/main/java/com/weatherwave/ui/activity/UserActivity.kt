package com.weatherwave.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weatherwave.R
import com.weatherwave.model.ApiResponse
import com.weatherwave.model.LaporanRequest
import com.weatherwave.network.RetrofitClient
import com.weatherwave.ui.fragment.MapsFragment
import com.weatherwave.ui.fragment.InformFragment
import com.weatherwave.ui.fragment.ProfileFragment
import com.weatherwave.ui.fragment.UserFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class UserActivity : AppCompatActivity() {

    private lateinit var bottomNavUser: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var ivTakenPhoto: ImageView
    private var imageBitmap: Bitmap? = null
    private var userId: Int = -1

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
        private const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)

        bottomNavUser = findViewById(R.id.bottomNavUser)
        fab = findViewById(R.id.fab)

        // Cek intent untuk fragment yang harus ditampilkan
        val fragmentId = intent.getIntExtra("FRAGMENT_ID", -1)
        if (fragmentId != -1) {
            bottomNavUser.selectedItemId = fragmentId // Atur item yang dipilih pada Bottom Navigation
        } else if (savedInstanceState == null) {
            loadFragment(UserFragment())  // Default Fragment jika tidak ada info dari intent
        }

        bottomNavUser.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    loadFragment(UserFragment())
                    true
                }
                R.id.maps -> {
                    loadFragment(MapsFragment())
                    true
                }
                R.id.news -> {
                    loadFragment(InformFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            showReportDialog()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showReportDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report, null)
        val edtProblemDescription = dialogView.findViewById<EditText>(R.id.edtProblemDescription)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnSubmitReport = dialogView.findViewById<Button>(R.id.btnSubmitReport)
        ivTakenPhoto = dialogView.findViewById(R.id.ivTakenPhoto)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.report_card)

        btnClose.setOnClickListener {
            dialog.dismiss() // Menutup dialog
        }

        btnSubmitReport.setOnClickListener {
            val problemDescription = edtProblemDescription.text.toString()
            if (problemDescription.isNotEmpty()) {
                sendReportToApi(problemDescription)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Harap isi deskripsi masalah", Toast.LENGTH_SHORT).show()
            }
        }

        ivTakenPhoto.setOnClickListener {
            showPhotoOptionsDialog()
        }

        dialog.show()
    }

    private fun showPhotoOptionsDialog() {
        val options = arrayOf("Ambil Foto dari Kamera", "Pilih Foto dari Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Foto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        } else {
            Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    ivTakenPhoto.setImageBitmap(imageBitmap)
                }
                REQUEST_GALLERY -> {
                    val imageUri = data?.data
                    ivTakenPhoto.setImageURI(imageUri)
                    imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
            }
        }
    }

    private fun sendReportToApi(problemDescription: String) {
        if (userId != -1) {
            val base64Image = imageBitmap?.let {
                val outputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }

            val laporanRequest = LaporanRequest(
                user_id = userId,
                jenis_masalah = problemDescription,
                foto = base64Image
            )

            RetrofitClient.instance.addLaporan(laporanRequest).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UserActivity, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UserActivity, "Gagal mengirim laporan: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@UserActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}