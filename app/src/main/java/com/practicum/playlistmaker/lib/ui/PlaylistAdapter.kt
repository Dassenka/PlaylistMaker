package com.practicum.playlistmaker.lib.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.favorite.domain.model.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>(){

    var listOfPlaylists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder    {
        return PlaylistViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listOfPlaylists[position])
    }
}