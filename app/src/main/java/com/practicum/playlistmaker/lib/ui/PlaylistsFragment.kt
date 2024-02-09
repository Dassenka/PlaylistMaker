package com.practicum.playlistmaker.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentListOfPlaylistsBinding
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.state.PlaylistsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var buttonCreateNewPlaylist: Button

    private val PlaylistsViewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentListOfPlaylistsBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private lateinit var playlists: RecyclerView

    private val adapter = PlaylistAdapter(
        object : PlaylistAdapter.PlaylistClickListener {
            override fun onPlaylistClick(playlist: Playlist) {
                openPlaylist(playlist)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListOfPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCreateNewPlaylist = binding.buttonCreateNewPlaylist

        buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.newPlaylistCreationFragment,
                NewPlaylistCreationFragment.createArgs(null, "playlistsFragment")
            )
        }

        playlists = binding.recyclerView
        playlists.layoutManager = GridLayoutManager(requireContext(), 2)
        playlists.adapter = adapter

        PlaylistsViewModel.fillData()
        PlaylistsViewModel.stateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        playlists.adapter = null
    }

    override fun onResume() {
        super.onResume()
        PlaylistsViewModel.fillData()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlist)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.recyclerView.visibility = View.GONE
        binding.placeholderMessage.visibility = View.VISIBLE

    }

    private fun showContent(playlist: List<Playlist>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.GONE

        adapter?.listOfPlaylists?.clear()
        adapter?.listOfPlaylists?.addAll(playlist)
        adapter?.notifyDataSetChanged()
    }

    private fun openPlaylist(playlist: Playlist) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaLibFragment_to_playlistInfoFragment,
                PlaylistInfoFragment.createArgs(playlist)
            )
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
        fun newInstance() = PlaylistsFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}