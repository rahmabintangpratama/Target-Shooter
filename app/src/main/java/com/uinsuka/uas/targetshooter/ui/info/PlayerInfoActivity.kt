package com.uinsuka.uas.targetshooter.ui.info

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityPlayerInfoBinding
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory
import com.uinsuka.uas.targetshooter.ui.custom.NicknameEditText
import com.uinsuka.uas.targetshooter.ui.main.MainActivity
import kotlin.system.exitProcess

class PlayerInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerInfoBinding
    private val viewModel: PlayerInfoViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000
    private lateinit var nicknameEditText: NicknameEditText
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        clickSoundId = soundPool.load(this, R.raw.click_button, 1)

        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        binding.btnSubmit.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            val nickname = binding.NicknameEditText.text.toString()

            nicknameEditText = binding.NicknameEditText

            val isNicknameValid = nickname.isNotBlank()

            if (isNicknameValid) {
                if (nickname.isNotEmpty()) {
                    viewModel.saveSession(nickname)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@PlayerInfoActivity,
                        "Nickname cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
                    finishAffinity()
                    exitProcess(0)
                } else {
                    Toast.makeText(
                        this@PlayerInfoActivity,
                        "Press once more to exit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
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