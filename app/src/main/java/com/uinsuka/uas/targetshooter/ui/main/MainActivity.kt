package com.uinsuka.uas.targetshooter.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
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

        binding.btnStart.setOnClickListener {
            when {
                binding.rbEasy.isChecked -> startActivity(Intent(this, EasyAActivity::class.java))
                binding.rbHard.isChecked -> startActivity(Intent(this, HardAActivity::class.java))
                else -> {
                    Toast.makeText(this@MainActivity, "You have to choose the difficulty level", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}