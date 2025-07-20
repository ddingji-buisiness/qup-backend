package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.entity.Clan
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.matching.enum.*
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "signals",
    indexes = [
        Index(name = "idx_signals_status_expires", columnList = "status, expires_at"),
        Index(name = "idx_signals_game_created", columnList = "game_id, created_at"),
        Index(name = "idx_signals_sender_status", columnList = "sender_id, status"),
        Index(name = "idx_signals_platform", columnList = "source_platform")
    ]
)
class Signal(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_clan_id")
    var senderClan: Clan? = null,

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "team_size", nullable = false)
    var teamSize: Int,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime
) : BaseTimeEntity() {

    @Column(name = "content", length = 1000)
    var content: String? = null

    @Column(name = "current_members", nullable = false)
    var currentMembers: Int = 1

    @Enumerated(EnumType.STRING)
    @Column(name = "source_platform", nullable = false, length = 20)
    var sourcePlatform: SourcePlatform = SourcePlatform.WEB

    @Enumerated(EnumType.STRING)
    @Column(name = "signal_type", nullable = false, length = 20)
    var signalType: SignalType = SignalType.INSTANT

    @Enumerated(EnumType.STRING)
    @Column(name = "match_mode", nullable = false, length = 20)
    var matchMode: MatchMode = MatchMode.RANKED

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SignalStatus = SignalStatus.ACTIVE

    @Enumerated(EnumType.STRING)
    @Column(name = "signal_scope", nullable = false, length = 20)
    var signalScope: SignalScope = SignalScope.INDIVIDUAL  // INDIVIDUAL, CLAN_INTERNAL, CLAN_VS_CLAN

    @Column(name = "clan_match_format", length = 100)
    var clanMatchFormat: String? = null  // "5v5", "3v3", "custom"

    @Column(name = "min_clan_level")
    var minClanLevel: Int? = null

    @Column(name = "max_clan_level")
    var maxClanLevel: Int? = null

    @Column(name = "voice_chat_required", nullable = false)
    var voiceChatRequired: Boolean = false

    @Column(name = "auto_accept", nullable = false)
    var autoAccept: Boolean = false

    @Column(name = "region", length = 50)
    var region: String? = null

    @Column(name = "language_preference", length = 10)
    var languagePreference: String? = null

    @Column(name = "play_time")
    var playTime: LocalDateTime? = null

    @Version
    var version: Long = 0

    // 클랜 매칭인지 확인
    fun isClanMatch(): Boolean = senderClan != null

    // 클랜 내부 매칭인지 확인
    fun isClanInternalMatch(): Boolean = signalScope == SignalScope.CLAN_INTERNAL

    // 클랜 간 대전인지 확인
    fun isClanVsClanMatch(): Boolean = signalScope == SignalScope.CLAN_VS_CLAN

    override fun toString(): String {
        return "Signal(id=$id, title='$title', status=$status, " +
                "platform=$sourcePlatform, members=$currentMembers/$teamSize)"
    }
}