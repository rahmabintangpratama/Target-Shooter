package com.uinsuka.uas.targetshooter.ui.main

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityMainBinding
import com.uinsuka.uas.targetshooter.settings.FontScaleSetting
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory
import com.uinsuka.uas.targetshooter.ui.about.AboutActivity
import com.uinsuka.uas.targetshooter.ui.change.ChangeNameActivity
import com.uinsuka.uas.targetshooter.ui.easy.EasyNarrationActivity
import com.uinsuka.uas.targetshooter.ui.hard.HardNarrationActivity
import com.uinsuka.uas.targetshooter.ui.info.PlayerInfoActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private var startSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(FontScaleSetting.updateBaseContextLocale(newBase))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FontScaleSetting.resetFontScale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.1f)
        val buttonScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.1f)
        val nicknameAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvNickname, buttonScaleX, buttonScaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        nicknameAnimator.start()
        val easyAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.rbEasy, buttonScaleX, buttonScaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        easyAnimator.start()
        val hardAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.rbHard, buttonScaleX, buttonScaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        hardAnimator.start()

        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)
        val aboutAnimator =
            ObjectAnimator.ofPropertyValuesHolder(binding.tvAbout, scaleX, scaleY).apply {
                duration = 800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        aboutAnimator.start()

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
        clickSoundId = soundPool.load(this, R.raw.click_button, 1)
        startSoundId = soundPool.load(this, R.raw.start_button, 1)

        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        binding.tvNickname.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            val intent = Intent(this, ChangeNameActivity::class.java)
            startActivity(intent)
        }

        binding.rbEasy.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
        }
        binding.rbHard.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
        }

        binding.btnStart.setOnClickListener {
            when {
                binding.rbEasy.isChecked -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    startActivity(Intent(this, EasyNarrationActivity::class.java))
                }

                binding.rbHard.isChecked -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    startActivity(Intent(this, HardNarrationActivity::class.java))
                }

                else -> {
                    soundPool.play(startSoundId, 1f, 1f, 1, 0, 1f)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.choose_difficulty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvAbout.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            startActivity(Intent(this, AboutActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
                    finishAffinity()
                    exitProcess(0)
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.exit), Toast.LENGTH_SHORT)
                        .show()
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