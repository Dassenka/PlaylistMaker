package com.practicum.playlistmaker


import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class PlayerActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var artworkUrl100: ImageView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var collectionNameGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val back: ImageButton = findViewById(R.id.backToSearchButton)

        back.setOnClickListener {
            finish()
        }

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        artworkUrl100 = findViewById(R.id.album_image)
        collectionName = findViewById(R.id.trackCollectionName)
        releaseDate = findViewById(R.id.trackReleaseDate)
        primaryGenreName = findViewById(R.id.trackPrimaryGenreName)
        country = findViewById(R.id.trackCountry)
        collectionNameGroup = findViewById(R.id.collectionNameGroup)

        track = intent.getSerializableExtra("PlayTrack") as Track

        playTrack(track)

    }

    private fun playTrack(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTimeFormat()
        if (track.collectionName.isNullOrEmpty()) {
            collectionNameGroup.visibility = View.GONE
        } else {
            collectionName.text = track.collectionName
        }

        releaseDate.text = track.getYearFromReleaseDate()
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        val albumImage = track.getCoverArtwork()
        Glide.with(artworkUrl100)
            .load(albumImage)
            .placeholder(R.drawable.ic_placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(artworkUrl100.resources.getDimensionPixelSize(R.dimen.album_corner_radius)))
            .into(artworkUrl100)
    }

}