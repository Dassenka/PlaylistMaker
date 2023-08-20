package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this)
        )[SettingsViewModel::class.java]

        val back: LinearLayout = findViewById(R.id.backToMainActivity)
        back.setOnClickListener {
            finish()
        }

        val share = findViewById<TextView>(R.id.share)
        share.setOnClickListener {
            viewModel.shareLink(getString(R.string.android_address))

        }

        val support = findViewById<TextView>(R.id.support)
        support.setOnClickListener {
            viewModel.openSupport(
                getString(R.string.support_address),
                getString(R.string.support_themes),
                getString(R.string.support_message)
            )
        }

        val agreement = findViewById<TextView>(R.id.agreement)
        agreement.setOnClickListener {
            viewModel.openTerms(getString(R.string.agreement_link))

        }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)

        viewModel.observeThemeSettingsLiveData.observe(this) { theme ->
            themeSwitcher.isChecked = theme.darkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchThemeVM(checked)
        }
    }
}