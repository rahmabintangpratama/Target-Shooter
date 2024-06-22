package com.uinsuka.uas.targetshooter.ui.info

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.uinsuka.uas.targetshooter.databinding.ActivityPlayerInfoBinding
import com.uinsuka.uas.targetshooter.ui.ViewModelFactory
import com.uinsuka.uas.targetshooter.ui.main.MainActivity

class PlayerInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerInfoBinding
    private lateinit var viewModel: PlayerInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[PlayerInfoViewModel::class.java]

        binding.btnSubmit.setOnClickListener {
            val nickname = binding.NicknameEditText.text.toString()
            if (nickname.isNotEmpty()) {
                viewModel.saveSession(nickname)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@PlayerInfoActivity, "Nickname cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}