package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel>()
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
    private lateinit var progressTrackTime: TextView
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonLike: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        artworkUrl100 = findViewById(R.id.album_image)
        collectionName = findViewById(R.id.trackCollectionName)
        releaseDate = findViewById(R.id.trackReleaseDate)
        primaryGenreName = findViewById(R.id.trackPrimaryGenreName)
        country = findViewById(R.id.trackCountry)
        collectionNameGroup = findViewById(R.id.collectionNameGroup)
        progressTrackTime = findViewById(R.id.progressTrackTime)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonLike = findViewById(R.id.likeButton)

        track = intent.getSerializableExtra(SearchFragment.PLAY_TRACK) as Track

        //Подписываемся на состояние плеера ViewModel
        viewModel.playerStateLiveData().observe(this) {
            render(it)
        }

        //Подписываемся на состояние таймера
        viewModel.timerCurrentPositionLiveData().observe(this) {
            changeTime(it)
        }

        //Подписываемся на состояние кнопки избранное
        viewModel.isFavoriteLiveData().observe(this) { isFavourite ->
            buttonLikeChanging(isFavourite)
        }

        viewModel.checkIdFavoriteTrack(track)

        installAttributes(track)

        // функция для подготовки медиаплеера
        viewModel.preparePlayer(track.previewUrl)

        val back: ImageButton = findViewById(R.id.backToSearchButton)
        back.setOnClickListener {
            finish()
        }

        buttonPlay.setOnClickListener {
            viewModel.playerState()
        }

        buttonLike.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        viewModel.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun installAttributes(track: Track) {
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

    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.STATE_PAUSED -> buttonPlay.setImageResource(R.drawable.ic_play_button)
            PlayerState.STATE_PLAYING -> buttonPlay.setImageResource(R.drawable.ic_pause_button)
            PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                buttonPlay.setImageResource(R.drawable.ic_play_button)
                progressTrackTime.setText(R.string.progressTrackTime)
            }
        }
    }

    private fun changeTime(currentTime: String) {
        progressTrackTime.text = currentTime
    }

    private fun buttonLikeChanging(isFavourite: Boolean) {
        if (isFavourite) {
            buttonLike.setImageResource(R.drawable.ic_like_button_press)
        }
        else {
            buttonLike.setImageResource(R.drawable.ic_like_button)
        }
    }
}