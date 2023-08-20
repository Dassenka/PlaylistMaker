package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track

class TrackViewHolder(
    parent: ViewGroup,
    private val clickListener: TrackAdapter.TrackClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
) {

    private var trackName: TextView = itemView.findViewById(R.id.trackName)
    private var artistName: TextView = itemView.findViewById(R.id.artistName)
    private var trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private var artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTimeFormat()

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(artworkUrl100.resources.getDimensionPixelSize(R.dimen.album_corner_radius)))
            .into(artworkUrl100)

        itemView.setOnClickListener { clickListener.onTrackClick(track) }
    }
}