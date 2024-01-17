package com.practicum.playlistmaker.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.model.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(){

    private lateinit var buttonCreateNewPlaylist: Button

    private val PlaylistsViewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!


    private var adapter: PlaylistAdapter ? = null
    private lateinit var playlists: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCreateNewPlaylist = binding.buttonCreateNewPlaylist

        buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistCreationFragment)
        }

        //PlaylistsViewModel.fillData()
        //PlaylistsViewModel.stateLiveData().observe(viewLifecycleOwner) {
        //    render(it)
        //}

        playlists = binding.recyclerView
        adapter = PlaylistAdapter()
        playlists.layoutManager = GridLayoutManager(requireContext(),2)
        playlists.adapter = adapter

        PlaylistsViewModel.fillData()
        PlaylistsViewModel.stateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
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

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}