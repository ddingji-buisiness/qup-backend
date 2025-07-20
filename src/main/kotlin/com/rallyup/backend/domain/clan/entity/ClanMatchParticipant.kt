package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_match_participants",
    indexes = [
        Index(name = "idx_clan_match_participants_signal_id", columnList = "signal_id"),
        Index(name = "idx_clan_match_participants_user_id", columnList = "user_id"),
        Index(name = "idx_clan_match_participants_deleted_at", columnList = "deleted_at")
    ]
)
class ClanMatchParticipant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    var signal: ClanMatchSignal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "joined_at", nullable = false)
    var joinedAt: LocalDateTime = LocalDateTime.now()

) : BaseTimeEntity()