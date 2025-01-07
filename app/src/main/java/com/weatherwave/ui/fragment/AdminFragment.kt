package com.weatherwave.ui.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.weatherwave.R

class AdminFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        // Lokasi baru berdasarkan koordinat yang diberikan
        val location = LatLng(-5.145657, 119.523120)
        googleMap.addMarker(MarkerOptions().position(location).title("Marker at New Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f)) // Atur zoom level sesuai kebutuhan
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inisialisasi peta
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
