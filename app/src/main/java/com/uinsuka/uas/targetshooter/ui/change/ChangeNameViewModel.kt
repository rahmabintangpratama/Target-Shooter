package com.uinsuka.uas.targetshooter.ui.change

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.data.pref.PlayerModel
import kotlinx.coroutines.launch

class ChangeNameViewModel(private val repository: PlayerRepository) : ViewModel() {

    fun getSession(): LiveData<PlayerModel> {
        return repository.getSession().asLiveData()
    }

    fun saveSession(nickname: String, bestScore: Int) {
        viewModelScope.launch {
            repository.saveSession(PlayerModel(nickname, true, bestScore))
        }
    }
}