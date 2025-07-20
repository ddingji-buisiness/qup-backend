package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "discord_signal_data",
    indexes = [
        Index(name = "idx_discord_signal_guild_channel", columnList = "discord_guild_id, discord_channel_id"),
        Index(name = "idx_discord_signal_message", columnList = "discord_message_id"),
        Index(name = "idx_discord_signal_bot_group", columnList = "discord_bot_group")
    ]
)
class DiscordSignalData(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    var signal: Signal,

    @Column(name = "discord_channel_id", length = 50, nullable = false)
    var discordChannelId: String,

    @Column(name = "discord_guild_id", length = 50, nullable = false)
    var discordGuildId: String
) : BaseTimeEntity() {

    @Column(name = "discord_message_id", length = 50)
    var discordMessageId: String? = null

    @Column(name = "discord_bot_group", length = 10)
    var discordBotGroup: String? = null // A, B 그룹

    @Column(name = "allow_discord_join", nullable = false)
    var allowDiscordJoin: Boolean = true

    @Column(name = "discord_team_balance_type", length = 20)
    var discordTeamBalanceType: String? = null // "balance", "random"

    @Column(name = "required_positions", columnDefinition = "JSON")
    var requiredPositions: String? = null

    @Column(name = "preferred_tiers", columnDefinition = "JSON")
    var preferredTiers: String? = null

    init {
        require(discordChannelId.isNotBlank()) { "Discord 채널 ID는 필수입니다." }
        require(discordGuildId.isNotBlank()) { "Discord 길드 ID는 필수입니다." }
    }
}