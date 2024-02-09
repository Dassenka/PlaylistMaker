package com.practicum.playlistmaker.lib.ui


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistCreationBinding
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import kotlinx.coroutines.launch

import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistCreationFragment : Fragment() {

    val requester = PermissionRequester.instance()

    //регистрируем событие, которое вызывает photo picker
    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .transform(
                        CenterCrop(),
                        RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.album_corner_radius))
                    )
                    .into(binding.buttonAddPhoto)

                filePath = uri
            }
        }

    private val newPlaylistCreationViewModel: NewPlaylistCreationViewModel by viewModel()
    private var _binding: FragmentNewPlaylistCreationBinding? = null
    private val binding get() = _binding!!

    private var confirmDialog: MaterialAlertDialogBuilder? = null
    private var playlistName = ""
    private var filePath: Uri? = null
    private var playlist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable("playlist") as? Playlist

        playlist?.let {
            setAttributesForEditingPlaylist(it)
            PARENT_FLAG = requireArguments().getString(PARENT_FLAG).toString()
        }

        binding.buttonCreateNewPlayList.setOnClickListener {
            if (playlist == null) {
                savePlayList()
            } else {
                lifecycleScope.launch {
                    editingPlaylist(playlist!!)
                }
            }
        }

        binding.buttonAddPhoto.setOnClickListener {
            checkPermission()
        }

        binding.InputPlayListName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonCreateNewPlayList.isEnabled = s?.isNotEmpty() == true
                playlistName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.backToPreviousFragment.setOnClickListener {
            backButtonClick()
        }

        // добавление слушателя для обработки нажатия на кнопку системную кнопку Back и отображения диалога
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backButtonClick()
                }
            })

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_dialog_new_playlist_title)
            .setMessage(R.string.confirm_dialog_new_playlist_message)
            .setNeutralButton(R.string.confirm_dialog_cancel) { dialog, which ->
            }
            .setNegativeButton(R.string.confirm_dialog_complete) { dialog, which ->
                backToPreviousFragment()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermission() {
        lifecycleScope.launch {
            requester.request(getCheckedStorageConst()).collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    is PermissionResult.Denied.DeniedPermanently -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data =
                            Uri.fromParts("package", requireContext().packageName, null)
                        requireContext().startActivity(intent)
                    }
                    is PermissionResult.Denied.NeedsRationale -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_needs_rationale),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is PermissionResult.Cancelled -> {
                        return@collect
                    }
                }
            }
        }
    }

    private fun getCheckedStorageConst(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            android.Manifest.permission.READ_MEDIA_IMAGES
        }else{
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun savePlayList() {
        val newPlayList = Playlist(
            0,
            playlistName,
            binding.InputPlayListDescription.text.toString(),
            newPlaylistCreationViewModel.savePlaylistPhotoToPrivateStorage(filePath),
            null,
            0
        )
        newPlaylistCreationViewModel.saveNewPlayList(newPlayList)
        toastCreatePlaylist()
        backToPreviousFragment()
    }


    private fun backButtonClick() {
        if (PARENT_FLAG != "playlistInfoFragment") {
            if (filePath != null ||
                playlistName.isNotEmpty() ||
                binding.InputPlayListDescription.text?.isNotEmpty() == true
            ) {
                confirmDialog?.show()
            } else {
                backToPreviousFragment()
            }
        } else {
            backToPreviousFragment()
        }
    }

    //смотрим откуда запускалось создание плейлиста
    private fun backToPreviousFragment() {
        if (PARENT_FLAG == "playerActivity") {
            requireActivity().findViewById<ConstraintLayout>(R.id.playerActivityLayout).isVisible =
                true
            parentFragmentManager.popBackStack()
            PARENT_FLAG = ""
        } else {
            findNavController().navigateUp()
            PARENT_FLAG = ""
        }
    }

    private fun toastCreatePlaylist() {
        // toast "Плейлист [название плейлиста] создан"
        Toast.makeText(
            requireContext(),
            getString(R.string.toast_confirm_create_playlist, playlistName),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setAttributesForEditingPlaylist(playlist: Playlist) {
        binding.headerTitle.setText(R.string.headerTitle_editing_playlist)
        binding.InputPlayListName.setText(playlist?.playlistName)
        binding.InputPlayListDescription.setText(playlist?.playlistDescription)
        binding.buttonCreateNewPlayList.setText(R.string.button_editing_playlist)
        if (playlist!!.playlistPhotoPath != null) {
            Glide.with(this)
                .load(playlist!!.playlistPhotoPath)
                .centerCrop()
                .transform(
                    CenterCrop(),
                    RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.album_corner_radius))
                )
                .into(binding.buttonAddPhoto)
        }

        binding.buttonCreateNewPlayList.isEnabled = true
    }

    private fun editingPlaylist(playlist: Playlist) {
        var updatedPlaylist: Playlist? = null
        val playlistId = playlist.playlistId
        val updatedName = binding.InputPlayListName.text.toString()
        val updatedDetails = binding.InputPlayListDescription.text.toString()
        val updatedListOfTracksId = playlist.listOfTracksId
        val updatedNumberOfTracks = playlist.numberOfTracks

        if (filePath != null) {
            updatedPlaylist = playlist.copy(
                playlistId,
                updatedName,
                updatedDetails,
                newPlaylistCreationViewModel.savePlaylistPhotoToPrivateStorage(filePath),
                updatedListOfTracksId,
                updatedNumberOfTracks
            )
            newPlaylistCreationViewModel.updatePlaylist(updatedPlaylist)
        } else {
            val updatedPhotoPath = playlist.playlistPhotoPath
            if (filePath == null) {
                updatedPlaylist = playlist.copy(
                    playlistId,
                    updatedName,
                    updatedDetails,
                    updatedPhotoPath,
                    updatedListOfTracksId,
                    updatedNumberOfTracks
                )
                newPlaylistCreationViewModel.updatePlaylist(updatedPlaylist)
            }
        }
        val bundle = Bundle()
        bundle.putSerializable("playlist", updatedPlaylist)
        parentFragmentManager.setFragmentResult("playlist", bundle)
        findNavController().popBackStack()
    }

    companion object {
        const val TAG = "NewPlaylistCreationFragment"
        private var PARENT_FLAG = ""
        private const val ARGS_PLAYLIST_ID = "playlist"

        fun newInstance(flagParentActivity: String): NewPlaylistCreationFragment {
            PARENT_FLAG = flagParentActivity
            return NewPlaylistCreationFragment()
        }

        fun createArgs(playlist: Playlist?, flagParentActivity: String): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlist, PARENT_FLAG to flagParentActivity)
    }
}