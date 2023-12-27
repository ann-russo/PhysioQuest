package com.example.physioquest.screens.account

data class AccountActions(
    val onHomeClick: () -> Unit,
    val onLeaderboardClick: () -> Unit,
    val onAccountClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onBackClick: () -> Unit
)

data class AccountData(
    val username: String,
    val rank: String,
    val userLevel: Int,
    val xpInCurrentLevel: Int,
    val xpNeededForNextLevel: Int
)