package com.rallyup.backend.domain.user.enum

enum class SocialProvider(
    val displayName: String,
    val iconUrl: String? = null
) {
    DISCORD("Discord", "discord-icon.png"),
    GOOGLE("Google", "google-icon.png"),
    KAKAO("Kakao", "kakao-icon.png"),
    APPLE("Apple", "apple-icon.png");

    companion object {
        fun fromString(provider: String): SocialProvider? {
            return values().find { it.name.equals(provider, ignoreCase = true) }
        }
    }
}