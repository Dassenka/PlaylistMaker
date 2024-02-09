package com.practicum.playlistmaker.sharing.domain.impl


import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp(shareAddress: String) {
        externalNavigator.shareLink(getShareAppLink(shareAddress))
    }

    override fun openTerms(agreementLink: String) {
        externalNavigator.openLink(getTermsLink(agreementLink))
    }

    override fun openSupport(
        supportAddress: String,
        supportTheme: String,
        supportMessage: String
    ) {
        externalNavigator.openEmail(
            getSupportEmailData(
                supportAddress,
                supportTheme,
                supportMessage
            )
        )
    }

    override fun sharePlaylist(message: String) {
        externalNavigator.sharePlaylist(message)
    }

    private fun getShareAppLink(shareAddress: String): String {
        return shareAddress

    }

    private fun getTermsLink(agreementLink: String): String {
        return agreementLink
    }

    private fun getSupportEmailData(
        supportAddress: String,
        supportTheme: String,
        supportMessage: String
    ): EmailData {
        return EmailData(
            supportAddress,
            supportTheme,
            supportMessage
        )
    }
}