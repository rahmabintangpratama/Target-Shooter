package com.uinsuka.uas.targetshooter.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: PlayerRepository) : ViewModel() {
    fun saveBestScore(score: Int) {
        viewModelScope.launch {
            repository.saveBestScore(score)
        }
    }
}