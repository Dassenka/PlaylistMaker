package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back: LinearLayout = findViewById(R.id.backToMainActivity)

        back.setOnClickListener {
            finish()
        }
    }
}