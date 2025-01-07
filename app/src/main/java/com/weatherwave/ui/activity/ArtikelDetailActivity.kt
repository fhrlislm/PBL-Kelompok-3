package com.weatherwave.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.weatherwave.R

class ArtikelDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_artikel)

        // Mendapatkan referensi ke komponen UI
        val tvJudulArtikel = findViewById<TextView>(R.id.tvJudulArtikel)
        val tvIsiArtikel = findViewById<TextView>(R.id.tvIsiArtikel)
        val btnBack = findViewById<ImageView>(R.id.backBtn)

        // Mendapatkan data dari intent
        val judul = intent.getStringExtra("judul") ?: "No Title"
        val isi = intent.getStringExtra("isi") ?: "No Content"

        // Menampilkan data ke dalam komponen UI
        tvJudulArtikel.text = judul
        tvIsiArtikel.text = isi

        // Menambahkan aksi untuk tombol kembali
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}
