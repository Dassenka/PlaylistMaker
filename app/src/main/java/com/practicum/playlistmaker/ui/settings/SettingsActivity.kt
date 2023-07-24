package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.App

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back: LinearLayout = findViewById(R.id.backToMainActivity)

        back.setOnClickListener {
            finish()
        }

        val share = findViewById<TextView>(R.id.share)
        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.android_address))
            }
            startActivity(Intent.createChooser(shareIntent, ""))
        }

        val support = findViewById<TextView>(R.id.support)
        support.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_themes))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            startActivity(supportIntent)
        }

        val agreement = findViewById<TextView>(R.id.agreement)
        agreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.agreement_link))
            startActivity(agreementIntent)
        }

        val themeSwitcher = findViewById<SwitchCompat>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}