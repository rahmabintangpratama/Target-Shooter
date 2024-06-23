package com.uinsuka.uas.targetshooter.ui.main

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityMainBinding
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory
import com.uinsuka.uas.targetshooter.ui.easy.EasyAActivity
import com.uinsuka.uas.targetshooter.ui.hard.HardAActivity
import com.uinsuka.uas.targetshooter.ui.info.PlayerInfoActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var soundPool: SoundPool
    private var startSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { player ->
            if (!player.isLogin) {
                startActivity(Intent(this, PlayerInfoActivity::class.java))
                finish()
            } else {
                binding.tvNickname.text = player.playerName
                binding.tvBestScoreValue.text = player.bestScore.toString()
            }
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        startSoundId = soundPool.load(this, R.raw.start_button, 1)

        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        binding.btnStart.setOnClickListener {
            when {
                binding.rbEasy.isChecked -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    startActivity(Intent(this, EasyAActivity::class.java))
                }
                binding.rbHard.isChecked -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    startActivity(Intent(this, HardAActivity::class.java))
                }
                else -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    Toast.makeText(this@MainActivity, "You have to choose the difficulty level", Toast.LENGTH_SHORT).show()
                }
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