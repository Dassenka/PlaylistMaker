package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(getShareAppLink: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getShareAppLink)
            context.startActivity(this)
        }
    }

    override fun openLink(getTermsLink: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getTermsLink)
        }
        context.startActivity(agreementIntent)
    }

    override fun openEmail(getSupportEmailData: EmailData) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getSupportEmailData.supportAddress))
            putExtra(Intent.EXTRA_SUBJECT, getSupportEmailData.supportTheme)
            putExtra(Intent.EXTRA_TEXT, getSupportEmailData.supportMessage)
            context.startActivity(this)
        }
    }
}