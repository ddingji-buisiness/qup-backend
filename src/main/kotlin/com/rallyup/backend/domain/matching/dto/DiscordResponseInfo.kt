package com.rallyup.backend.domain.matching.dto

import java.time.LocalDateTime

data class DiscordResponseInfo(
    val responseId: Long,
    val signalId: Long,
    val responderUserId: Long,
    val responderUsername: String,
    val discordUserId: String?,
    val discordUsername: String?,
    val preferredPosition: String?,
    val message: String?,
    val compatibilityScore: Double?,
    val status: String,
    val respondedAt: LocalDateTime
)