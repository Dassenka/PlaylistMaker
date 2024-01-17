package com.practicum.playlistmaker.lib.ui


import android.content.Intent
import android.net.Uri
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
    private var filePath : Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreateNewPlayList.setOnClickListener {
            savePlayList()
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
            requester.request(android.Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
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
        if (filePath != null ||
            playlistName.isNotEmpty() ||
            binding.InputPlayListDescription.text?.isNotEmpty() == true
        ) {
            confirmDialog?.show()
        } else {
            backToPreviousFragment()
        }
    }

    //смотрим откуда запускалось создание плейлиста
    private fun backToPreviousFragment() {
        if (parentActivityPlayer) {
            requireActivity().findViewById<ConstraintLayout>(R.id.playerActivityLayout).isVisible = true
            parentFragmentManager.popBackStack()
            parentActivityPlayer = false
        } else {
        findNavController().navigateUp()
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

    companion object {
        const val TAG = "NewPlaylistCreationFragment"

        private var parentActivityPlayer = false
        fun newInstance(flagParentActivity: Boolean): NewPlaylistCreationFragment {
            parentActivityPlayer = flagParentActivity
            return NewPlaylistCreationFragment()
        }
    }
}