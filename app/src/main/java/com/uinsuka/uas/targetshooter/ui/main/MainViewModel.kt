package com.uinsuka.uas.targetshooter.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.data.pref.PlayerModel

class MainViewModel(private val repository: PlayerRepository) : ViewModel() {
    fun getSession(): LiveData<PlayerModel> {
        return repository.getSession().asLiveData()
    }
}