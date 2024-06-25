package com.uinsuka.uas.targetshooter.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.uinsuka.uas.targetshooter.databinding.ActivitySplashBinding
import com.uinsuka.uas.targetshooter.ui.welcome.WelcomeActivity
import kotlin.system.exitProcess

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            showUnsupportedVersionDialog()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                goToWelcomeActivity()
            }, SPLASH_DELAY)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
                    finishAffinity()
                    exitProcess(0)
                } else {
                    Toast.makeText(this@SplashActivity, "Press once more to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun showUnsupportedVersionDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Unsupported Android Version")
            .setMessage("This game only supports Android 9 (Pie) and above.\nThe application will close automatically in 5 seconds.")
            .setCancelable(false)
            .create()

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            finishAffinity()
            exitProcess(0)
        }, CLOSE_DELAY)
    }

    private fun goToWelcomeActivity() {
        Intent(this, WelcomeActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private companion object {
        private const val SPLASH_DELAY = 2500L
        private const val CLOSE_DELAY = 5000L
    }
}