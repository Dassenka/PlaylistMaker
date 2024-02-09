package com.practicum.playlistmaker.lib.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.state.PlaylistInfoState
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private val PlaylistInfoViewModel: PlaylistInfoViewModel by viewModel()
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private lateinit var listOfTracksRecycler: RecyclerView
    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlist: Playlist? = null

    private val adapter = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                startActivity(track)
            }
        },
        object : TrackAdapter.LongTrackClickListener {
            override fun onTrackLongClick(track: Track): Boolean {
                onItemLongClick(track)
                return true
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable("playlist") as? Playlist
        PlaylistInfoViewModel.getPlayListById(playlist!!.playlistId)

        listOfTracksRecycler = binding.listOfTracksRecycler
        listOfTracksRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listOfTracksRecycler.adapter = adapter


        PlaylistInfoViewModel.stateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.backToPreviousFragment.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.SharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.MoreInPlaylist.setOnClickListener {
            playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            showMenuMore(playlist!!)
        }

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        binding.tracksBottomSheet.post {
            val shareLocation = IntArray(2)
            binding.SharePlaylist.getLocationOnScreen(shareLocation)
            val bottomSheetheight =
                binding.root.height - shareLocation[1] - resources.getDimensionPixelSize(R.dimen.margin_top_tracks_bottomSheet)
            tracksBottomSheetBehavior!!.peekHeight = bottomSheetheight.coerceAtLeast(100)
        }


        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // добавление слушателя для обработки нажатия на кнопку системную кнопку Back и отображения диалога
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (playlistBottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN){
                        playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                    }else {
                        findNavController().popBackStack()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    private fun render(state: PlaylistInfoState) {
        when (state) {
            is PlaylistInfoState.Content -> showContent(state.playlist)
            is PlaylistInfoState.Empty -> showEmpty()
            is PlaylistInfoState.fillTracksRecycler -> showTracks(state.tracks)
            is PlaylistInfoState.updateTracksRecycler -> updateTracks(state.tracks)
            is PlaylistInfoState.DeletePlaylist -> showDeletePlaylist()
            is PlaylistInfoState.Error -> showError()
        }
    }

    private fun showError() {
        findNavController().popBackStack()
        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
    }

    private fun showEmpty() {
        binding.listOfTracksRecycler.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.playlistDuration.text = getString(R.string.playlist_time)
        binding.TracksCount.text = getString(R.string.playlist_no_tracks)
    }

    private fun showDeletePlaylist() {
        findNavController().popBackStack()
    }

    private fun showTracks(listOfTracks: List<Track>) {
        val tracksWithArtworkUrl60 = listOfTracks.map { track ->
            track.copy(artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "60x60bb.jpg"))
        }

        adapter.track.clear()
        adapter.track.addAll(tracksWithArtworkUrl60)
        adapter.notifyDataSetChanged()

        binding.playlistDuration.text = PlaylistInfoViewModel.getPlaylistTrackTime(adapter.track)
    }

    private fun updateTracks(listOfTracks: List<Track>) {
        val updatedTracks = listOfTracks.map { track ->
            track.copy(artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "60x60bb.jpg"))
        }
        adapter.track.clear()
        adapter.track.addAll(updatedTracks)
        adapter.notifyDataSetChanged()

        binding.playlistDuration.text = PlaylistInfoViewModel.getPlaylistTrackTime(adapter.track)
        binding.TracksCount.text =
            PlaylistInfoViewModel.formatNumberOfTracks(adapter.track.size)

    }

    private fun showContent(playlist: Playlist) {
        PlaylistInfoViewModel.getTracksFromPlaylist(playlist)

        val albumImage = playlist.playlistPhotoPath
        Glide.with(binding.albumImage)
            .load(albumImage)
            .placeholder(R.drawable.ic_placeholder_player)
            .centerCrop()
            .into(binding.albumImage)

        binding.playlistName.text = playlist.playlistName

        if (playlist.playlistDescription.isNullOrEmpty()) {
            binding.playlistDescription.visibility = View.GONE
        } else {
            binding.playlistDescription.text = playlist.playlistDescription
        }
        if (playlist.numberOfTracks == 0) {
            binding.TracksCount.text = getString(R.string.playlist_time)
        } else {
            binding.TracksCount.text =
                PlaylistInfoViewModel.formatNumberOfTracks(playlist.numberOfTracks)
        }
    }

    private fun sharePlaylist() {
        if (playlist?.numberOfTracks!! > 0) {
            PlaylistInfoViewModel.sharePlaylist(
                playlist!!,
                adapter.track
            )
        } else {
            Toast.makeText(
                requireContext(),
                context?.getString(R.string.no_tracks_for_share),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showMenuMore(playlist: Playlist) {
        binding.apply {
            playlistCard.playlistInPlayerCardName.text = playlist.playlistName
            playlistCard.playlistInPlayerCardNumberOfTracks.text =
                PlaylistInfoViewModel.formatNumberOfTracks(playlist.numberOfTracks)

            Glide.with(requireActivity())
                .load(playlist.playlistPhotoPath)
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        playlistCard.playlistInPlayerCardPhoto.resources.getDimensionPixelSize(
                            R.dimen.recycler_view_corner_radius
                        )
                    )
                )
                .into(playlistCard.playlistInPlayerCardPhoto)

            sharePlaylist.setOnClickListener {
                playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                sharePlaylist()
            }

            editingPlaylist.setOnClickListener {
                playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

                lifecycleScope.launch {
                    delay(500L)
                    findNavController().navigate(
                        R.id.action_playlistInfoFragment_to_newPlaylistCreationFragment,
                        NewPlaylistCreationFragment.createArgs(playlist, "playlistInfoFragment")
                    )
                }
            }

            deletePlaylist.setOnClickListener {
                playlistBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.getString(R.string.delete_playlist))
                    .setMessage(context?.getString(R.string.delete_playlist_dialog_message))
                    .setNegativeButton(context?.getString(R.string.delete_playlist_dialog_no)) { _, _ ->
                    }
                    .setPositiveButton(context?.getString(R.string.delete_playlist_dialog_yes)) { _, _ ->
                        PlaylistInfoViewModel.deletePlaylist(playlist)
                    }.show()
            }
        }
    }

    //ограничение нажатия на элементы списка не чаще одного раза в секунду
    private fun clickDebounce(): Boolean {
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

    //функция вызова PlayerActivity для TrackAdapter
    fun startActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(PLAY_TRACK, track)
            startActivity(intent)
        }
    }

    fun onItemLongClick(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(context?.getString(R.string.delete_track_dialog_title))
            .setMessage(context?.getString(R.string.delete_track_dialog_message))
            .setNeutralButton(context?.getString(R.string.confirm_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete_track_dialog_delete)) { _, _ ->
                PlaylistInfoViewModel.deleteTrackFromPlaylist(playlist, track.trackId)
            }
            .setOnDismissListener {
            }
            .show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAY_TRACK = "PLAY_TRACK"
        private const val ARGS_PLAYLIST_ID = "playlist"

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlist)
    }
}