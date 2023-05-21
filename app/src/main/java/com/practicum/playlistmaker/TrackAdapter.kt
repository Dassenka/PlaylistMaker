package com.practicum.playlistmaker


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView



class TrackAdapter (var track: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var historyList = ArrayList<Track>()
    private var searchHistory = SearchHistory(historyList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(track[position])
        holder.itemView.setOnClickListener { //слушатель нажатия на трэк
            searchHistory.addTrackInHistory(track[position]) //функция добавления трэка
            val playerIntent = Intent(it.context, PlayerActivity::class.java)
            playerIntent.putExtra("PlayTrack", track[position])// передаем трэк в новую активити
            it.context.startActivity(playerIntent)
        }
    }

    override fun getItemCount(): Int {
        return track.size
    }
}