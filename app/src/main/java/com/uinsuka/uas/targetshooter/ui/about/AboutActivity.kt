package com.uinsuka.uas.targetshooter.ui.about

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.uinsuka.uas.targetshooter.R
import com.uinsuka.uas.targetshooter.databinding.ActivityAboutBinding
import com.uinsuka.uas.targetshooter.settings.FontScaleSetting

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var scrollView: ScrollView
    private val handler = Handler(Looper.getMainLooper())
    private var isScrollingDown = true
    private var isUserScrolling = false
    private val scrollSpeed = 1
    private val scrollDelay = 10L
    private var isAutoScrolling = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(FontScaleSetting.updateBaseContextLocale(newBase))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FontScaleSetting.resetFontScale(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        scrollView = binding.scrollView
        startAutoScroll()

        scrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isUserScrolling = true
                    stopAutoScroll()
                }

                MotionEvent.ACTION_UP -> {
                    isUserScrolling = false
                    startAutoScroll()
                }
            }
            false
        }
    }

    private fun startAutoScroll() {
        if (isAutoScrolling) return
        isAutoScrolling = true
        handler.post(object : Runnable {
            override fun run() {
                if (!isUserScrolling) {
                    val maxScroll = scrollView.getChildAt(0).height - scrollView.height
                    val currentScroll = scrollView.scrollY
                    if (isScrollingDown) {
                        if (currentScroll < maxScroll) {
                            scrollView.smoothScrollBy(0, scrollSpeed)
                        } else {
                            isScrollingDown = false
                        }
                    } else {
                        if (currentScroll > 0) {
                            scrollView.smoothScrollBy(0, -scrollSpeed)
                        } else {
                            isScrollingDown = true
                        }
                    }
                }
                if (isAutoScrolling) {
                    handler.postDelayed(this, scrollDelay)
                }
            }
        })
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        stopAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        startAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}