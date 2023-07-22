package com.practicum.playlistmaker.ui.search

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.domain.model.Track

class TrackAdapter(var track: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var historyList = ArrayList<Track>()
    private var searchHistory = SearchHistory(historyList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(track[position])
        holder.itemView.setOnClickListener { //слушатель нажатия на трэк
            if (clickDebounce()) {
                searchHistory.addTrackInHistory(track[position]) //функция добавления трэка
                SearchActivity.startActivity(track[position], it.context) //вызываем новую активити
            }
        }
    }

    override fun getItemCount(): Int {
        return track.size
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    fun clickDebounce() : Boolean { // ограничение нажатия на элементы списка не чаще одного раза в секунду
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L // ограничение нажатия на элементы списка не чаще одного раза в секунду
    }
}