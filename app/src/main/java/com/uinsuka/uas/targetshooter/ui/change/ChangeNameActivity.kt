package com.uinsuka.uas.targetshooter.ui.change

import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityChangeNameBinding
import com.uinsuka.uas.targetshooter.settings.FontScaleSetting
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory

class ChangeNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeNameBinding
    private val viewModel: ChangeNameViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var currentBestScore: Int = 0

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(FontScaleSetting.updateBaseContextLocale(newBase))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FontScaleSetting.resetFontScale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSound()
        setupMediaPlayer()
        setupViewModel()
        setupActions()
    }

    private fun setupSound() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        clickSoundId = soundPool.load(this, R.raw.click_button, 1)
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun setupViewModel() {
        viewModel.getSession().observe(this) { player ->
            binding.NicknameEditText.setText(player.playerName)
            currentBestScore = player.bestScore
        }
    }

    private fun setupActions() {
        binding.btnSubmit.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            val newName = binding.NicknameEditText.text.toString()

            if (newName.isNotBlank()) {
                viewModel.saveSession(newName, currentBestScore)
                Toast.makeText(
                    this,
                    getString(R.string.name_changed_success),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.error_empty_nickname),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}