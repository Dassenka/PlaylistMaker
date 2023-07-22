package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.search.SearchActivity
import com.practicum.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


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
    private lateinit var progressTrackTime: TextView

    private lateinit var buttonPlay: ImageButton
    private var mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

    private lateinit var mainThreadHandler: Handler
    private val timer = createUpdateTimerTask()

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
        progressTrackTime = findViewById(R.id.progressTrackTime)
        buttonPlay = findViewById(R.id.buttonPlay)

        track = intent.getSerializableExtra(SearchActivity.PLAY_TRACK) as Track

        installAttributes(track)

        mainThreadHandler =
            Handler(Looper.getMainLooper()) // Создаём Handler, привязанный к ГЛАВНОМУ потоку для отсчета времени воспроизведения

        preparePlayer()

        buttonPlay.setOnClickListener {

            mediaPlayerInteractor.playbackControl({
                buttonPlay.setImageResource(R.drawable.ic_pause_button)
                mainThreadHandler.post(timer)
            }, {
                mainThreadHandler.removeCallbacks(timer)
                buttonPlay.setImageResource(R.drawable.ic_play_button)
            })
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pausePlayer({ buttonPlay.setImageResource(R.drawable.ic_play_button) })
        mainThreadHandler.removeCallbacks(timer)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(timer)
        mediaPlayerInteractor.release()
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

    private fun preparePlayer() { // функция для подготовки медиаплеера
        mediaPlayerInteractor.preparePlayer(track.previewUrl, {
            buttonPlay.setImageResource(R.drawable.ic_play_button)
            progressTrackTime.setText(R.string.progressTrackTime)
        }, {
            buttonPlay.setImageResource(R.drawable.ic_play_button)
            progressTrackTime.setText(R.string.progressTrackTime)
            mainThreadHandler.removeCallbacks(timer)
        }
        )
    }

    private fun createUpdateTimerTask(): Runnable { //таймер проигрывателя
        return object : Runnable {
            override fun run() {
                progressTrackTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format((mediaPlayerInteractor.currentPosition()))
                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val DELAY = 500L
    }
}