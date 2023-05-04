package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    private var trackName: TextView = itemView.findViewById(R.id.trackName)
    private var artistName: TextView = itemView.findViewById(R.id.artistName)
    private var trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private var artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(artworkUrl100)
    }
}