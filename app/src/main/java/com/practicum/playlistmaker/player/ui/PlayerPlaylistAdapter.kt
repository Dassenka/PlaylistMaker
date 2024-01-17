package com.practicum.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.favorite.domain.model.Playlist

class PlayerPlaylistAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    var listOfPlaylists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder =
        PlayerPlaylistViewHolder(parent, clickListener)


    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(listOfPlaylists[position])
    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }

    interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}