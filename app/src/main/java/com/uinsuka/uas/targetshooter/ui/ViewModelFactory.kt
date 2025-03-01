package com.uinsuka.uas.targetshooter.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uinsuka.uas.targetshooter.data.PlayerRepository
import com.uinsuka.uas.targetshooter.di.Injection
import com.uinsuka.uas.targetshooter.ui.change.ChangeNameViewModel
import com.uinsuka.uas.targetshooter.ui.info.PlayerInfoViewModel
import com.uinsuka.uas.targetshooter.ui.main.MainViewModel
import com.uinsuka.uas.targetshooter.ui.result.ResultViewModel
import com.uinsuka.uas.targetshooter.ui.welcome.WelcomeViewModel

class ViewModelFactory(private val repository: PlayerRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PlayerInfoViewModel::class.java) -> {
                PlayerInfoViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ChangeNameViewModel::class.java) -> {
                ChangeNameViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(repository) as T
            }

            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}