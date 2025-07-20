package com.rallyup.backend.domain.matching.dto

data class DiscordParticipantInfo(
    val userId: Long,
    val username: String,
    val discordUserId: String?,
    val discordUsername: String?,
    val position: String?,
    val teamName: String?
)
