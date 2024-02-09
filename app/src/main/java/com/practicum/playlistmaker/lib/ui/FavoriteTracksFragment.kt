package com.practicum.playlistmaker.lib.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.lib.state.FavoriteTracksState
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val adapter = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                startActivity(track)
            }
        },
        object : TrackAdapter.LongTrackClickListener {
            override fun onTrackLongClick(track: Track): Boolean {
                return true
            }
        }
    )

    private lateinit var placeholderMessage: LinearLayout
    private lateinit var favoriteList: RecyclerView
    private lateinit var errorMessage: TextView
    private var isClickAllowed = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderMessage = binding.placeholderMessage
        favoriteList = binding.favoriteList
        errorMessage = binding.errorMessage

        favoriteList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoriteList.adapter = adapter

        favoriteTracksViewModel.fillData()

        favoriteTracksViewModel.stateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        favoriteTracksViewModel.fillData()
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state.tracks)
            is FavoriteTracksState.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        favoriteList.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        errorMessage.text = message
    }

    private fun showContent(tracks: List<Track>) {
        favoriteList.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE

        adapter.track.clear()
        adapter.track.addAll(tracks)
        adapter.notifyDataSetChanged()
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

    //функция вызова PlayerActivity для TrackAdapter
    fun startActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(PLAY_TRACK, track)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAY_TRACK = "PLAY_TRACK"
    }
}