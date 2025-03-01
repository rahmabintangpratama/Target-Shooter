package com.uinsuka.uas.targetshooter.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.data.pref.PlayerModel

class WelcomeViewModel(private val repository: PlayerRepository) : ViewModel() {
    fun getSession(): LiveData<PlayerModel> {
        return repository.getSession().asLiveData()
    }
}