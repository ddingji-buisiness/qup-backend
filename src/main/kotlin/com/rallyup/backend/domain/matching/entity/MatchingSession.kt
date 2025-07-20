package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.matching.dto.DiscordBotSessionInfo
import com.rallyup.backend.domain.matching.dto.DiscordParticipantInfo
import com.rallyup.backend.domain.matching.enum.JoinStatus
import com.rallyup.backend.domain.matching.enum.SessionStatus
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(
    name = "matching_sessions",
    indexes = [
        Index(name = "idx_matching_sessions_signal", columnList = "signal_id"),
        Index(name = "idx_matching_sessions_status", columnList = "status"),
        Index(name = "idx_matching_sessions_code", columnList = "session_code")
    ]
)
class MatchingSession(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    var signal: Signal,

    @Column(name = "session_code", unique = true, length = 20, nullable = false)
    var sessionCode: String
) : BaseTimeEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SessionStatus = SessionStatus.CREATED

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null

    @Column(name = "ended_at")
    var endedAt: LocalDateTime? = null

    @Column(name = "max_duration_minutes", nullable = false)
    var maxDurationMinutes: Int = 240

    @Column(name = "auto_start", nullable = false)
    var autoStart: Boolean = true

    @Version
    var version: Long = 0

    init {
        require(sessionCode.length in 4..20) { "세션 코드는 4-20자 사이여야 합니다." }
        require(maxDurationMinutes > 0) { "최대 지속 시간은 0보다 커야 합니다." }
    }
}