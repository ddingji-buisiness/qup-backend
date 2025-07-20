package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "discord_response_data",
    indexes = [
        Index(name = "idx_discord_response_user", columnList = "discord_user_id"),
        Index(name = "idx_discord_response_message", columnList = "discord_message_id")
    ]
)
class DiscordResponseData(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    var response: SignalResponse,

    @Column(name = "discord_user_id", length = 50, nullable = false)
    var discordUserId: String,

    @Column(name = "discord_username", length = 100, nullable = false)
    var discordUsername: String
) : BaseTimeEntity() {

    @Column(name = "discord_message_id", length = 50)
    var discordMessageId: String? = null

    @Column(name = "discord_display_name", length = 100)
    var discordDisplayName: String? = null

    init {
        require(discordUserId.isNotBlank()) { "Discord 사용자 ID는 필수입니다." }
        require(discordUsername.isNotBlank()) { "Discord 사용자명은 필수입니다." }
    }
}