package com.weatherwave.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.weatherwave.R
import com.weatherwave.model.Artikel
import com.weatherwave.ui.adapter.ArtikelAdapter

class InformFragment : Fragment(R.layout.fragment_inform) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewArtikel)
        val btnBerita = view.findViewById<MaterialButton>(R.id.btnBerita)
        val btnArtikel = view.findViewById<MaterialButton>(R.id.btnArtikel)

        // Data dummy untuk berita
        val beritaList = List(10) {
            Artikel(
                "nelayan", // Nama drawable
                "Media Nasional",
                "Ini adalah contoh berita terbaru yang informatif dan relevan untuk Anda..."
            )
        }

        // Data dummy untuk artikel
        val kontenLists = List(10) {
            Artikel(
                "nelayan", // Nama drawable
                "Kompas News",
                "Ini adalah contoh artikel yang mendalam, memberikan wawasan yang berguna untuk pembaca..."
            )
        }

        // Setup RecyclerView dengan data awal (default: Konten)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ArtikelAdapter(kontenLists, isCardLayout = true)
        recyclerView.adapter = adapter

        // Logika untuk tombol "Berita"
        btnBerita.setOnClickListener {
            adapter.updateData(beritaList) // Ganti data RecyclerView dengan daftar berita
        }

        // Logika untuk tombol "Konten"
        btnArtikel.setOnClickListener {
            adapter.updateData(kontenLists) // Ganti data RecyclerView dengan daftar artikel
        }
    }
}
