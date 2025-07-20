package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.StatisticsPeriod
import com.rallyup.backend.domain.game.entity.Game
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_statistics",
    indexes = [
        Index(name = "idx_clan_statistics_clan", columnList = "clan_id"),
        Index(name = "idx_clan_statistics_game", columnList = "game_id"),
        Index(name = "idx_clan_statistics_date", columnList = "statistics_date")
    ]
)
class ClanStatistics(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    var game: Game? = null,

    @Column(name = "statistics_date", nullable = false)
    var statisticsDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 10)
    var periodType: StatisticsPeriod // DAILY, WEEKLY, MONTHLY
) : BaseTimeEntity() {

    @Column(name = "matches_played", nullable = false)
    var matchesPlayed: Int = 0

    @Column(name = "matches_won", nullable = false)
    var matchesWon: Int = 0

    @Column(name = "total_points_earned", nullable = false)
    var totalPointsEarned: Int = 0

    @Column(name = "active_members", nullable = false)
    var activeMembers: Int = 0

    @Column(name = "new_members", nullable = false)
    var newMembers: Int = 0

    @Column(name = "left_members", nullable = false)
    var leftMembers: Int = 0

    @Column(name = "applications_received", nullable = false)
    var applicationsReceived: Int = 0

    @Column(name = "applications_accepted", nullable = false)
    var applicationsAccepted: Int = 0

    @Column(name = "average_session_duration_minutes")
    var averageSessionDurationMinutes: Double? = null

    fun getWinRate(): Double {
        return if (matchesPlayed > 0) matchesWon.toDouble() / matchesPlayed else 0.0
    }

    fun getAcceptanceRate(): Double {
        return if (applicationsReceived > 0) applicationsAccepted.toDouble() / applicationsReceived else 0.0
    }
}