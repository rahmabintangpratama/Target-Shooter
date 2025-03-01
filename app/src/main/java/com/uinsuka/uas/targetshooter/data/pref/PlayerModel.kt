package com.uinsuka.uas.targetshooter.data.pref

data class PlayerModel(

    val playerName: String,
    val isLogin: Boolean,
    val bestScore: Int = 0
)