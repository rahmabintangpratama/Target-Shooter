package com.uinsuka.uas.targetshooter.di

import android.content.Context
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.data.pref.PlayerPreferences
import com.uinsuka.uas.targetshooter.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): PlayerRepository {
        val pref = PlayerPreferences.getInstance(context.dataStore)
        return PlayerRepository.getInstance(pref)
    }
}