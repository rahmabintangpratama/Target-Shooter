package com.uinsuka.uas.targetshooter.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class PlayerPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(player: PlayerModel) {
        dataStore.edit { preferences ->
            preferences[PLAYER_NAME] = player.playerName
            preferences[IS_LOGIN] = true
        }
    }

    fun getSession(): Flow<PlayerModel> {
        return dataStore.data.map { preferences ->
            PlayerModel(
                preferences[PLAYER_NAME] ?: "",
                preferences[IS_LOGIN] ?: false,
                preferences[BEST_SCORE] ?: 0
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveBestScore(score: Int) {
        dataStore.edit { preferences ->
            val currentBestScore = preferences[BEST_SCORE] ?: 0
            if (score > currentBestScore) {
                preferences[BEST_SCORE] = score
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PlayerPreferences? = null

        private val PLAYER_NAME = stringPreferencesKey("player_name")
        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val BEST_SCORE = intPreferencesKey("best_score")

        fun getInstance(dataStore: DataStore<Preferences>): PlayerPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = PlayerPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}