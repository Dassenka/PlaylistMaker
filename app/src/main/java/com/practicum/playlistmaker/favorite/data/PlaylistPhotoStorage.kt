package com.practicum.playlistmaker.favorite.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistPhotoStorage(private val context: Context) {

    fun savePlaylistPhotoToPrivateStorage(uri: Uri?) : String? {

        if (uri != null) {
            val filePath = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistPhotoAlbum"
            )

            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val timeCreation =
                SimpleDateFormat("dd.MM.yyyy_hh:mm:ss", Locale.getDefault()).format(Date().time)

            val file = File(filePath, "album_photo_$timeCreation.jpg")

            val inputStream = context.contentResolver.openInputStream(uri)

            val outputStream = FileOutputStream(file)

            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            return file.toUri().toString()
        }
        return null
    }
}