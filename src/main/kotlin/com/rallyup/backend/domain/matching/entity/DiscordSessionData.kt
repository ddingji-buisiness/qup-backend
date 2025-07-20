package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "discord_session_data",
    indexes = [
        Index(name = "idx_discord_session_guild_group", columnList = "discord_guild_id, discord_bot_group"),
        Index(name = "idx_discord_session_message", columnList = "discord_session_message_id")
    ]
)
class DiscordSessionData(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: MatchingSession,

    @Column(name = "discord_guild_id", length = 50, nullable = false)
    var discordGuildId: String
) : BaseTimeEntity() {

    @Column(name = "discord_bot_group", length = 10)
    var discordBotGroup: String? = null

    @Column(name = "discord_balance_type", length = 20)
    var discordBalanceType: String? = null

    @Column(name = "discord_session_message_id", length = 50)
    var discordSessionMessageId: String? = null

    @Column(name = "discord_team_channels", columnDefinition = "JSON")
    var discordTeamChannels: String? = null

    @Column(name = "discord_team_assignments", columnDefinition = "JSON")
    var discordTeamAssignments: String? = null

    init {
        require(discordGuildId.isNotBlank()) { "Discord 길드 ID는 필수입니다." }
    }
}