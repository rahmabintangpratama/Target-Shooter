package com.uinsuka.uas.targetshooter.data

import com.uinsuka.uas.targetshooter.data.pref.PlayerModel
import com.uinsuka.uas.targetshooter.data.pref.PlayerPreferences
import kotlinx.coroutines.flow.Flow

class PlayerRepository private constructor(
    private val playerPreferences: PlayerPreferences
) {

    suspend fun saveSession(player: PlayerModel) {
        playerPreferences.saveSession(player)
    }

    fun getSession(): Flow<PlayerModel> {
        return playerPreferences.getSession()
    }

    suspend fun logout() {
        playerPreferences.logout()
    }

    suspend fun saveBestScore(score: Int) {
        playerPreferences.saveBestScore(score)
    }

    companion object {
        @Volatile
        private var instance: PlayerRepository? = null

        fun getInstance(
            playerPreferences: PlayerPreferences
        ): PlayerRepository =
            instance ?: synchronized(this) {
                instance ?: PlayerRepository(playerPreferences)
            }.also { instance = it }
    }
}