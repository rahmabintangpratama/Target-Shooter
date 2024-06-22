package com.uinsuka.uas.targetshooter.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.data.pref.PlayerModel
import kotlinx.coroutines.launch

class PlayerInfoViewModel(private val repository: PlayerRepository) : ViewModel() {
    fun saveSession(nickname: String) {
        viewModelScope.launch {
            repository.saveSession(PlayerModel(nickname, true))
        }
    }
}