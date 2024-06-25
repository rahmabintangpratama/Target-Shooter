package com.uinsuka.uas.targetshooter.ui.result

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityResultBinding
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory
import com.uinsuka.uas.targetshooter.ui.main.MainActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
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

        mediaPlayer = MediaPlayer.create(this, R.raw.final_score)
        mediaPlayer.isLooping = false

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)
        val scoreAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvFinalScoreValue, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        scoreAnimator.start()

        val scoreA = intent.getIntExtra("scoreA", 0)
        val scoreB = intent.getIntExtra("scoreB", 0)
        val scoreC = intent.getIntExtra("scoreC", 0)
        val totalScore = scoreA + scoreB + scoreC

        binding.tvFinalScoreValue.text = totalScore.toString()
        viewModel.saveBestScore(totalScore)

        binding.btnHome.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ResultActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
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