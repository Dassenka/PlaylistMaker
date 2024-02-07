package com.practicum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.model.Track

class TrackAdapter(private val clickListener: TrackClickListener,
                   val longClickListener: LongTrackClickListener
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var track = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent, clickListener, longClickListener)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(track[position])
    }

    override fun getItemCount(): Int {
        return track.size
    }

    interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    interface LongTrackClickListener {
        fun onTrackLongClick(track: Track): Boolean
    }
}