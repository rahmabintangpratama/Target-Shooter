package com.uinsuka.uas.targetshooter.ui.easy

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityEasyBBinding
import com.uinsuka.uas.targetshooter.settings.FontScaleSetting
import com.uinsuka.uas.targetshooter.ui.main.MainActivity
import kotlin.random.Random

class EasyBActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEasyBBinding
    private lateinit var targets: List<ImageView>
    private lateinit var gunActors: List<ImageView>
    private lateinit var lasers: List<View>
    private var score = 0
    private var isGameRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var previousTarget: ImageView? = null
    private var timeLeft = 30
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var clickSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var scoreA = 0

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(FontScaleSetting.updateBaseContextLocale(newBase))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FontScaleSetting.resetFontScale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEasyBBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scoreA = intent.getIntExtra("scoreA", 0)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundId = soundPool.load(this, R.raw.target_hit, 1)
        clickSoundId = soundPool.load(this, R.raw.click_button, 1)

        targets = listOf(
            binding.btnTarget1, binding.btnTarget2, binding.btnTarget3,
            binding.btnTarget4, binding.btnTarget5, binding.btnTarget6,
            binding.btnTarget7, binding.btnTarget8, binding.btnTarget9
        )

        gunActors = listOf(
            binding.gunActor1, binding.gunActor2, binding.gunActor3
        )

        lasers = listOf(
            binding.laser1, binding.laser2, binding.laser3,
            binding.laser4, binding.laser5, binding.laser6,
            binding.laser7, binding.laser8, binding.laser9
        )

        mediaPlayer = MediaPlayer.create(this, R.raw.start_game)
        mediaPlayer.isLooping = true

        updateScoreDisplay()
        updateTimeLeftDisplay()
        startGame()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun startGame() {
        isGameRunning = true
        score = 0
        timeLeft = 30
        updateScoreDisplay()
        updateTimeLeftDisplay()
        showGunActors()
        showRandomTarget()
        startTimer()

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun showGunActors() {
        gunActors.forEach { it.visibility = View.VISIBLE }
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

        hideLasers()

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

        val laserIndex = targets.indexOf(target)
        if (laserIndex != -1) {
            lasers[laserIndex].visibility = View.VISIBLE
        }

        handler.postDelayed({
            target.visibility = View.GONE
            target.setImageResource(R.drawable.ic_target)

            if (laserIndex != -1) {
                lasers[laserIndex].visibility = View.GONE
            }
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

        hideGunActors()
        hideLasers()

        mediaPlayer.pause()
        mediaPlayer.seekTo(0)

        binding.btnNextStage.visibility = View.VISIBLE
        binding.btnNextStage.setOnClickListener {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
            val intent = Intent(this, EasyCActivity::class.java)
            intent.putExtra("scoreA", scoreA)
            intent.putExtra("scoreB", score)
            startActivity(intent)
            finish()
        }
    }

    private fun hideGunActors() {
        gunActors.forEach { it.visibility = View.GONE }
    }

    private fun hideLasers() {
        lasers.forEach { it.visibility = View.GONE }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
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
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)

        soundPool.release()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}