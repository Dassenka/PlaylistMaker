package com.practicum.playlistmaker

import android.media.MediaPlayer
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
import java.text.SimpleDateFormat
import java.util.*

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
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private lateinit var mainThreadHandler: Handler

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

        mainThreadHandler = Handler(Looper.getMainLooper()) // Создаём Handler, привязанный к ГЛАВНОМУ потоку для отсчета времени воспроизведения

        preparePlayer()

        buttonPlay.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        mainThreadHandler.removeCallbacks(createUpdateTimerTask())
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
        playerState = STATE_PREPARED
        mediaPlayer.release()
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
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
            progressTrackTime.setText(R.string.progressTrackTime)
        }
        mediaPlayer.setOnCompletionListener { //будет вызываться автоматически после завершения воспроизведения
            playerState = STATE_PREPARED
            mainThreadHandler.removeCallbacks(createUpdateTimerTask())
            buttonPlay.setImageResource(R.drawable.ic_play_button)
            progressTrackTime.setText(R.string.progressTrackTime)
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_pause_button)
        playerState = STATE_PLAYING
        mainThreadHandler.post(
            createUpdateTimerTask()
        )
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        mainThreadHandler.removeCallbacks(createUpdateTimerTask())
        buttonPlay.setImageResource(R.drawable.ic_play_button)
    }

    private fun createUpdateTimerTask(): Runnable { //таймер проигрывателя
        return object : Runnable {
            override fun run() {
                progressTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format((mediaPlayer.currentPosition))
                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }
}