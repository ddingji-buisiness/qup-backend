package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "clan_discord_integrations")
class ClanDiscordIntegration(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "discord_guild_id", length = 50, nullable = false)
    var discordGuildId: String
) : BaseTimeEntity() {

    @Column(name = "discord_invite_code", length = 20)
    var discordInviteCode: String? = null

    @Column(name = "auto_role_assignment", nullable = false)
    var autoRoleAssignment: Boolean = true

    @Column(name = "sync_nicknames", nullable = false)
    var syncNicknames: Boolean = false

    @Column(name = "webhook_url", length = 2048)
    var webhookUrl: String? = null
}