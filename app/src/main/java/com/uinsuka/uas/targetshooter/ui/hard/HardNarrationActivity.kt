package com.uinsuka.uas.targetshooter.ui.hard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityHardNarrationBinding

class HardNarrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHardNarrationBinding
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private val animationDuration: Long = 1000
    private val transitionDelay: Long = 300
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHardNarrationBinding.inflate(layoutInflater)
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

        mediaPlayer = MediaPlayer.create(this, R.raw.intro)
        mediaPlayer.isLooping = true

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        setupViews()
        setupListeners()
        setupAnimator()
    }

    private fun setupViews() {
        binding.ivOperator.visibility = View.GONE
        binding.cardViewMessage1.visibility = View.GONE
        binding.cardViewMessage2.visibility = View.GONE
        binding.cardViewMessage3.visibility = View.GONE
        binding.cardViewMessage4.visibility = View.GONE
        binding.tvSkip.visibility = View.VISIBLE

        showMessage(1)
    }

    private fun setupListeners() {
        binding.tvNext1.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            disableNextButtons()
            showMessage(2)
        }
        binding.tvNext2.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            disableNextButtons()
            showMessage(3)
        }
        binding.tvNext3.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            disableNextButtons()
            showMessage(4)
        }
        binding.tvNext4.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            disableNextButtons()
            goToHardAActivity()
        }
        binding.tvSkip.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            goToHardAActivity()
        }
    }

    private fun disableNextButtons() {
        binding.tvNext1.isClickable = false
        binding.tvNext2.isClickable = false
        binding.tvNext3.isClickable = false
        binding.tvNext4.isClickable = false
    }

    private fun enableNextButton(messageNumber: Int) {
        when (messageNumber) {
            1 -> binding.tvNext1.isClickable = true
            2 -> binding.tvNext2.isClickable = true
            3 -> binding.tvNext3.isClickable = true
            4 -> binding.tvNext4.isClickable = true
        }
    }

    private fun setupAnimator() {
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.1f)
        val next1Animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvNext1, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        next1Animator.start()

        val next2Animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvNext2, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        next2Animator.start()

        val next3Animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvNext3, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        next3Animator.start()

        val next4Animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvNext4, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        next4Animator.start()

        val skipAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvSkip, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        skipAnimator.start()
    }

    private fun showMessage(messageNumber: Int) {
        if (messageNumber > 1) {
            hideMessage(messageNumber - 1)
            Handler(Looper.getMainLooper()).postDelayed({
                showNewMessage(messageNumber)
            }, animationDuration + transitionDelay)
        } else {
            showNewMessage(messageNumber)
        }
    }

    private fun showNewMessage(messageNumber: Int) {
        val operator = binding.ivOperator
        val cardView = when (messageNumber) {
            1 -> binding.cardViewMessage1
            2 -> binding.cardViewMessage2
            3 -> binding.cardViewMessage3
            4 -> binding.cardViewMessage4
            else -> return
        }

        operator.visibility = View.VISIBLE
        operator.alpha = 0f
        cardView.visibility = View.VISIBLE
        cardView.alpha = 0f

        val fadeIn = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(operator, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f)
            )
            duration = animationDuration
        }

        fadeIn.start()

        if (messageNumber == 4) {
            binding.tvSkip.visibility = View.GONE
        }

        Handler(Looper.getMainLooper()).postDelayed({
            enableNextButton(messageNumber)
        }, animationDuration)
    }

    private fun hideMessage(messageNumber: Int) {
        val operator = binding.ivOperator
        val cardView = when (messageNumber) {
            1 -> binding.cardViewMessage1
            2 -> binding.cardViewMessage2
            3 -> binding.cardViewMessage3
            4 -> binding.cardViewMessage4
            else -> return
        }

        val fadeOut = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(operator, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(cardView, "alpha", 1f, 0f)
            )
            duration = animationDuration
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    cardView.visibility = View.GONE
                }
            })
        }

        fadeOut.start()
    }

    private fun goToHardAActivity() {
        startActivity(Intent(this, HardAActivity::class.java))
        finish()
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