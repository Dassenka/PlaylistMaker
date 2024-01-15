package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.model.PlaylistsState
import com.practicum.playlistmaker.lib.ui.NewPlaylistCreationFragment
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private lateinit var addTrackButton: ImageView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var buttonCreateNewPlayListBottomSheet: Button
    private var isClickAllowed = true
    private lateinit var playlists: RecyclerView
    private var playlistName = ""
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val adapter = PlayerPlaylistAdapter(
        object : PlayerPlaylistAdapter.PlaylistClickListener {
            override fun onPlaylistClick(playlist: Playlist) {
                addTrackInPlaylist(playlist, track)
            }
        }
    )

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
        addTrackButton = findViewById(R.id.addTrackButton)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        buttonCreateNewPlayListBottomSheet = findViewById(R.id.buttonCreateNewPlayListBottomSheet)

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

        //Подписываемся на состояние добавления трэка в избранное
        viewModel.isTrackInPlaylistLiveData().observe(this) { isAdded ->
            toastMessage(isAdded)
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

        playlists = findViewById(R.id.playlistInPlayer)
        playlists.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlists.adapter = adapter

        //bottomSheet
        val bottomSheetContainer = findViewById<LinearLayout>(R.id.playerBottomSheet)
        val overlay = findViewById<View>(R.id.overlay)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        addTrackButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            overlay.visibility = View.VISIBLE
            viewModel.fillData()

        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        viewModel.statePlaylistsLiveData().observe(this) {
            renderPlaylists(it)
        }

        //вызов NewPlaylistCreationFragment
        buttonCreateNewPlayListBottomSheet.setOnClickListener {
            supportFragmentManager.commit {
                replace(
                    R.id.playerActivityFragmentContainer,
                    NewPlaylistCreationFragment.newInstance(true),
                    NewPlaylistCreationFragment.TAG
                )
                addToBackStack(NewPlaylistCreationFragment.TAG)

                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                val playerActivityLayout = findViewById<ConstraintLayout>(R.id.playerActivityLayout)
                playerActivityLayout.isVisible = false
            }
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
        } else {
            buttonLike.setImageResource(R.drawable.ic_like_button)
        }
    }

    private fun renderPlaylists(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlist)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        playlists.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
    }

    private fun showContent(playlist: List<Playlist>) {
        playlists.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE

        adapter?.listOfPlaylists?.clear()
        adapter?.listOfPlaylists?.addAll(playlist)
        adapter?.notifyDataSetChanged()
    }


    fun addTrackInPlaylist(playlist: Playlist, track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackInPlaylist(playlist, track)
            playlistName = playlist.playlistName
        }
    }

    private fun toastMessage(isAdded: Boolean) {
        if (isAdded) {
            Toast.makeText(
                this,
                getString(R.string.toast_confirm_add_track_in_playlist, playlistName),
                Toast.LENGTH_LONG
            ).show()
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        } else {
            Toast.makeText(
                this,
                getString(R.string.toast_denied_add_track_in_playlist, playlistName),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //ограничение нажатия на элементы списка не чаще одного раза в секунду
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false

            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}