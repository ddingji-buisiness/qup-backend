package com.rallyup.backend.domain.matching.dto

data class DiscordBotSessionInfo(
    val sessionId: Long,
    val sessionCode: String,
    val guildId: String?,
    val botGroup: String?,
    val gameId: Long,
    val gameName: String,
    val teamSize: Int,
    val balanceType: String?,
    val participants: List<DiscordParticipantInfo>,
    val status: String
)
