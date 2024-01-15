package com.practicum.playlistmaker.lib.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorite.domain.model.Playlist

class PlaylistViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_card, parent, false)
    ) {

    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val numberOfTracks: TextView = itemView.findViewById(R.id.numberOfTracks)
    private val playlistPhoto: ImageView = itemView.findViewById(R.id.playlistPhoto)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.playlistName
        numberOfTracks.text = formatNumberOfTracks(playlist.numberOfTracks)

        Glide.with(itemView)
            .load(playlist.playlistPhotoPath)
            .placeholder(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(playlistPhoto.resources.getDimensionPixelSize(R.dimen.playlist_corner_radius))
            )
            .into(playlistPhoto)
    }

    private fun formatNumberOfTracks(numberOfTracks: Int): String {
        return when {
            numberOfTracks % 100 in 11..14 -> "$numberOfTracks треков"
            numberOfTracks % 10 == 1 -> "$numberOfTracks трек"
            numberOfTracks % 10 in 2..4 -> "$numberOfTracks трека"
            else -> "$numberOfTracks треков"
        }
    }
}