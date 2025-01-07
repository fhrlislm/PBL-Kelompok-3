package com.weatherwave.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.weatherwave.R
import com.weatherwave.ui.activity.OnboardingActivity

class OnboardingFragment : Fragment() {
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        setupViews(view)
        return view
    }

    private fun setupViews(view: View) {
        val btnNext = view.findViewById<Button>(R.id.btnNext) // Casted correctly as Button
        val btnSkip = view.findViewById<TextView>(R.id.btnSkip) // Correctly casted as TextView
        val btnStart = view.findViewById<Button>(R.id.btnStart) // Casted correctly as Button

        // Setup content
        view.findViewById<ImageView>(R.id.imgIllustration).setImageResource(getImageForPosition(position))
        view.findViewById<TextView>(R.id.txtWelcome).text = getTitleForPosition(position)
        view.findViewById<TextView>(R.id.txtDescription).text = getDescriptionForPosition(position)

        // Setup button visibility and actions based on the position
        when (position) {
            0, 1 -> {
                btnNext.visibility = View.VISIBLE
                btnSkip.visibility = View.VISIBLE
                btnStart.visibility = View.GONE

                btnNext.setOnClickListener { (activity as? OnboardingActivity)?.navigateToNextPage() }
                btnSkip.setOnClickListener { (activity as? OnboardingActivity)?.skipOnboarding() }
            }
            2 -> {
                btnNext.visibility = View.GONE
                btnSkip.visibility = View.GONE
                btnStart.visibility = View.VISIBLE

                btnStart.setOnClickListener { (activity as? OnboardingActivity)?.finishOnboarding() }
            }
        }
    }

    private fun getTitleForPosition(position: Int): String = when (position) {
        0 -> "Ketahui Kondisi Cuaca Laut Sekarang"
        1 -> "Tanggap Darurat Digenggamanmu"
        else -> "Selamat Datang di WeatherWave!"
    }

    private fun getDescriptionForPosition(position: Int): String = when (position) {
        0 -> "Akses informasi real-time tentang kecepatan angin, ketinggian gelombang, dan arah arus langsung dari ponsel Anda. Informasi ini esensial untuk navigasi yang aman dan memastikan Anda selalu siap menghadapi kondisi laut."
        1 -> "Aplikasi ini memungkinkan nelayan untuk segera terhubung dengan instansi terkait jika mengalami darurat di laut. Dengan fitur pelacakan real-time, bantuan bisa cepat diberikan, meningkatkan keselamatan nelayan secara efektif."
        else -> "Dengan data cuaca yang akurat dan tepat waktu, Anda dapat mengantisipasi perubahan kondisi laut dan membuat keputusan yang lebih bijaksana untuk perjalanan yang aman."
    }

    private fun getImageForPosition(position: Int): Int = when (position) {
        0 -> R.drawable.onboarding1
        1 -> R.drawable.onboarding2
        else -> R.drawable.onboarding3
    }

    companion object {
        private const val ARG_POSITION = "position"

        @JvmStatic
        fun newInstance(position: Int) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
    }
}

