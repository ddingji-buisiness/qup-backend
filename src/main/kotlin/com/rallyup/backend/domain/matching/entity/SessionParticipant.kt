package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.matching.enum.JoinStatus
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(
    name = "session_participants",
    indexes = [
        Index(name = "idx_session_participants_session_status", columnList = "session_id, join_status"),
        Index(name = "idx_session_participants_user", columnList = "user_id"),
        Index(name = "idx_session_participants_discord", columnList = "discord_user_id")
    ]
)
class SessionParticipant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: MatchingSession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "is_leader", nullable = false)
    var isLeader: Boolean = false
) : BaseTimeEntity() {

    @Column(name = "position", length = 50)
    var position: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status", nullable = false, length = 20)
    var joinStatus: JoinStatus = JoinStatus.INVITED

    @Column(name = "joined_at")
    var joinedAt: LocalDateTime? = null

    @Column(name = "left_at")
    var leftAt: LocalDateTime? = null

    @Column(name = "last_activity_at", nullable = false)
    var lastActivityAt: LocalDateTime = LocalDateTime.now()

    // Discord 정보는 별도 테이블로 분리 가능
    @Column(name = "discord_user_id", length = 50)
    var discordUserId: String? = null

    @Column(name = "discord_username", length = 100)
    var discordUsername: String? = null

    @Column(name = "discord_team_name", length = 20)
    var discordTeamName: String? = null

    @Version
    var version: Long = 0
}