package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val share = binding.share
        share.setOnClickListener {
            viewModel.shareLink(getString(R.string.android_address))
        }

        val support = binding.support
        support.setOnClickListener {
            viewModel.openSupport(
                getString(R.string.support_address),
                getString(R.string.support_themes),
                getString(R.string.support_message)
            )
        }

        val agreement = binding.agreement
        agreement.setOnClickListener {
            viewModel.openTerms(getString(R.string.agreement_link))

        }

        val themeSwitcher = binding.themeSwitcher
        viewModel.themeSettingsLiveData.observe(viewLifecycleOwner) { theme ->
            themeSwitcher.isChecked = theme.darkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchThemeVM(checked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}