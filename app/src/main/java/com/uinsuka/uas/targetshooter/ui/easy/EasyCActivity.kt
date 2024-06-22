package com.uinsuka.uas.targetshooter.ui.easy

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityEasyCBinding
import kotlin.random.Random

class EasyCActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEasyCBinding
    private lateinit var targets: List<ImageView>
    private var score = 0
    private var isGameRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var previousTarget: ImageView? = null
    private var timeLeft = 30
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEasyCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundId = soundPool.load(this, R.raw.caulk_gun, 1)

        targets = listOf(
            binding.btnTarget1, binding.btnTarget2, binding.btnTarget3,
            binding.btnTarget4, binding.btnTarget5, binding.btnTarget6,
            binding.btnTarget7, binding.btnTarget8, binding.btnTarget9,
            binding.btnTarget10, binding.btnTarget11, binding.btnTarget12
        )

        mediaPlayer = MediaPlayer.create(this, R.raw.game_music_loop)
        mediaPlayer.isLooping = true

        updateScoreDisplay()
        updateTimeLeftDisplay()
        startGame()
    }

    private fun startGame() {
        isGameRunning = true
        score = 0
        timeLeft = 30
        updateScoreDisplay()
        updateTimeLeftDisplay()
        showRandomTarget()
        startTimer()

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun startTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                timeLeft--
                updateTimeLeftDisplay()
                if (timeLeft > 0 && isGameRunning) {
                    handler.postDelayed(this, 1000)
                } else {
                    endGame()
                }
            }
        }, 1000)
    }

    private fun showRandomTarget() {
        if (!isGameRunning) return

        val randomTarget = getRandomTarget()
        randomTarget.setImageResource(R.drawable.ic_target)
        randomTarget.visibility = View.VISIBLE

        randomTarget.setOnClickListener {
            if (randomTarget.visibility == View.VISIBLE && isGameRunning) {
                hitTarget(randomTarget)
            }
        }

        val randomDuration = Random.nextLong(900, 1200)
        handler.postDelayed({
            if (randomTarget.visibility == View.VISIBLE) {
                randomTarget.visibility = View.GONE
            }
            if (isGameRunning) {
                showRandomTarget()
            }
        }, randomDuration)
    }

    private fun getRandomTarget(): ImageView {
        var randomTarget: ImageView
        do {
            randomTarget = targets[Random.nextInt(targets.size)]
        } while (randomTarget == previousTarget)

        previousTarget?.visibility = View.GONE
        previousTarget = randomTarget
        return randomTarget
    }

    private fun hitTarget(target: ImageView) {
        target.setOnClickListener(null)

        target.setImageResource(R.drawable.ic_target_broken)
        score++
        updateScoreDisplay()

        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)

        handler.postDelayed({
            target.visibility = View.GONE
            target.setImageResource(R.drawable.ic_target)
        }, 200)
    }

    private fun updateScoreDisplay() {
        binding.tvScoreValue.text = score.toString()
    }

    private fun updateTimeLeftDisplay() {
        binding.tvTimeLeftValue.text = timeLeft.toString()
    }

    private fun endGame() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        targets.forEach { it.visibility = View.VISIBLE }

        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)

        soundPool.release()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}