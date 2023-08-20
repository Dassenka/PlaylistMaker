package com.practicum.playlistmaker.sharing.domain

import android.content.Intent

interface SharingInteractor {
    fun shareApp(shareAddress: String)
    fun openTerms(agreementLink: String)
    fun openSupport(
        supportAddress: String,
        supportTheme: String,
        supportMessage: String
    )
}