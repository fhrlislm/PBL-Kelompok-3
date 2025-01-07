package com.weatherwave.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weatherwave.R
import com.weatherwave.model.Artikel
import com.weatherwave.ui.activity.ArtikelDetailActivity

class ArtikelAdapter(
    private var artikelList: List<Artikel>,
    private val isCardLayout: Boolean // Menentukan tampilan yang akan digunakan
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CARD = 0
        private const val VIEW_TYPE_INFO = 1
    }

    // ViewHolder untuk Layout Card
    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val articleImage: ImageView = view.findViewById(R.id.image_artikel)
        private val articleTitle: TextView = view.findViewById(R.id.tvJudul)
        private val articleSource: TextView = view.findViewById(R.id.tvSumber)

        @SuppressLint("DiscouragedApi")
        fun bind(artikel: Artikel) {
            val context = itemView.context
            val drawableId = context.resources.getIdentifier(artikel.image, "drawable", context.packageName)

            if (drawableId != 0) {
                articleImage.setImageResource(drawableId)
            } else {
                articleImage.setImageResource(R.drawable.haze) // Gambar default
            }
            articleTitle.text = artikel.title
            articleSource.text = artikel.source
        }
    }

    // ViewHolder untuk Layout Info
    inner class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val Image: ImageView = view.findViewById(R.id.articleImage)
        private val title: TextView = view.findViewById(R.id.articleTitle)
        private val source: TextView = view.findViewById(R.id.articleSource)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val artikel = artikelList[position]
                    val context = itemView.context
                    val intent = Intent(context, ArtikelDetailActivity::class.java).apply {
                        putExtra("judul", artikel.title)
                        putExtra("isi", artikel.source) // Misalkan source digunakan sebagai isi artikel
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(artikel: Artikel) {
            val context = itemView.context
            val drawableId = context.resources.getIdentifier(artikel.image, "drawable", context.packageName)

            if (drawableId != 0) {
                Image.setImageResource(drawableId)
            } else {
                Image.setImageResource(R.drawable.haze) // Gambar default
            }

            title.text = artikel.title
            source.text = artikel.source
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCardLayout) VIEW_TYPE_CARD else VIEW_TYPE_INFO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CARD -> {
                val view = inflater.inflate(R.layout.item_info, parent, false)
                CardViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_card, parent, false)
                InfoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val artikel = artikelList[position]
        when (holder) {
            is CardViewHolder -> holder.bind(artikel)
            is InfoViewHolder -> holder.bind(artikel)
        }
    }

    override fun getItemCount(): Int = artikelList.size

    // Metode untuk memperbarui data
    fun updateData(newData: List<Artikel>) {
        artikelList = newData
        notifyDataSetChanged()
    }
}
