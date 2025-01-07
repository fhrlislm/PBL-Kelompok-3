package com.weatherwave.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.weatherwave.R
import com.weatherwave.model.Artikel
import com.weatherwave.model.WeatherResponse
import com.weatherwave.network.ApiService
import com.weatherwave.ui.activity.LoginActivity
import com.weatherwave.ui.adapter.ArtikelAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var artikelList: List<Artikel>
    private lateinit var recyclerView: RecyclerView

    private lateinit var tvWindSpeed: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvPrecipitation: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvGreeting: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvWeatherDate: TextView
    private lateinit var tvWeatherCondition: TextView
    private lateinit var weatherIcon: ImageView

    private lateinit var alertLayout: LinearLayout
    private lateinit var alertIcon: ImageView
    private lateinit var alertText: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupRecyclerView(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSharedPreferences()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            checkLocationSettingsAndStartLocationUpdates()
        }
    }

    private fun bindViews(view: View) {
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvPrecipitation = view.findViewById(R.id.tvPrecipitation)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvLocation = view.findViewById(R.id.location_text)
        tvWeatherDate = view.findViewById(R.id.weather_date)
        tvWeatherCondition = view.findViewById(R.id.weather_condition)
        weatherIcon = view.findViewById(R.id.weather_icon)

        alertLayout = view.findViewById(R.id.alert_layout)
        alertIcon = view.findViewById(R.id.iv_safe_icon)
        alertText = view.findViewById(R.id.tv_safe_condition)
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rvNews)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        artikelList = listOf(
            Artikel("nelayan", "Cuaca ekstrem picu kecelakaan di Maluku", "Kompas News"),
            Artikel("nelayan", "Gempa mengguncang Maluku Utara", "Antara News"),
            Artikel("nelayan", "Banjir besar di Sulawesi", "CNN Indonesia")
        )
        recyclerView.adapter = ArtikelAdapter(artikelList, isCardLayout = false)
    }

    private fun setupSharedPreferences() {
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", AppCompatActivity.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        if (name != null) {
            tvGreeting.text = "Halo, $name!"
        } else {
            lifecycleScope.launch {
                if (isAdded) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

    private fun checkLocationSettingsAndStartLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            lifecycleScope.launch {
                getCurrentLocation()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(exception.resolution.intentSender, 1001, null, 0, 0, 0, null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(context, "Error starting location settings resolution: ${sendEx.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getCurrentLocation() {
        withContext(Dispatchers.IO) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    updateWeatherAndLocation(it)
                } ?: withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Tidak dapat menemukan lokasi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateWeatherAndLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        lifecycleScope.launch {
            fetchWeatherData(latitude, longitude)
            reverseGeocoding(latitude, longitude)
        }
    }

    private suspend fun reverseGeocoding(latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses.first()
                val locationText = "${address.subLocality ?: "Tidak diketahui"}, ${address.locality ?: "Tidak diketahui"}"
                withContext(Dispatchers.Main) {
                    tvLocation.text = locationText
                }
            } else {
                withContext(Dispatchers.Main) {
                    tvLocation.text = "Lokasi tidak ditemukan"
                }
            }
        }
    }

    private suspend fun fetchWeatherData(latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ApiService::class.java)
            try {
                val response = service.getCurrentWeather("e919982d399649dc91b195931240312", "$latitude,$longitude").execute()
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    withContext(Dispatchers.Main) {
                        if (weatherData != null) {
                            tvTemperature.text = "${weatherData.current.temp_c.toInt()}°C"
                            tvWeatherDate.text = formatWeatherDate(weatherData.location.localtime)
                            tvWeatherCondition.text = weatherData.current.condition.text
                            weatherIcon.setImageResource(getWeatherIcon(weatherData.current.condition.text))
                            tvWindSpeed.text = "${weatherData.current.wind_kph} km/h"
                            tvHumidity.text = "${weatherData.current.humidity}%"
                            tvPrecipitation.text = "${weatherData.current.precip_mm} mm"
                            updateWeatherAlert(weatherData)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Gagal memuat data cuaca", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateWeatherAlert(weatherData: WeatherResponse) {
        val windSpeed = weatherData.current.wind_kph
        val humidity = weatherData.current.humidity
        val rainfall = weatherData.current.precip_mm
        val weatherCondition = weatherData.current.condition.text

        val status: String
        val iconRes: Int
        val backgroundColor: Int

        when {
            windSpeed <= 5 && humidity <= 75 && rainfall == 0.0 && weatherCondition == "Clear" -> {
                status = "Wilayah kamu sekarang dalam kondisi aman."
                iconRes = R.drawable.correct
                backgroundColor = R.color.safe
            }
            windSpeed.toInt() in 6..20 || humidity.toInt() in 76..90 || rainfall > 0 -> {
                status = "Harap waspada, cuaca dalam keadaan kurang baik."
                iconRes = R.drawable.caution
                backgroundColor = R.color.warn
            }
            else -> {
                status = "Peringatan! Kondisi cuaca buruk, harap hindari melaut."
                iconRes = R.drawable.alarm
                backgroundColor = R.color.danger
            }
        }

        alertText.text = status
        alertIcon.setImageResource(iconRes)
        alertLayout.setBackgroundResource(R.drawable.bg_alert)
        val drawable = alertLayout.background as LayerDrawable
        val colorDrawable = drawable.findDrawableByLayerId(R.id.background_color) as GradientDrawable
        colorDrawable.setColor(resources.getColor(backgroundColor, null))
    }

    private fun formatWeatherDate(apiDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale("id"))
        val date: Date = inputFormat.parse(apiDate)!!
        return outputFormat.format(date)
    }

    private fun getWeatherIcon(condition: String): Int {
        return when (condition.lowercase(Locale.getDefault())) {
            "clear", "sunny" -> R.drawable.sunny
            "partly cloudy" -> R.drawable.partly_cloudy
            "cloudy" -> R.drawable.cloudy
            "fog" -> R.drawable.fog
            "haze" -> R.drawable.haze
            "heavy rain" -> R.drawable.heavy_rain
            "rain" -> R.drawable.rain
            "snow" -> R.drawable.snow
            "storm" -> R.drawable.storm
            "tornado" -> R.drawable.tornado
            else -> R.drawable.cloudy // default
        }
    }
}
