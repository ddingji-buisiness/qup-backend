package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ClanRankingTier
import com.rallyup.backend.domain.game.entity.Game
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_rankings",
    indexes = [
        Index(name = "idx_clan_rankings_game_score", columnList = "game_id, total_score"),
        Index(name = "idx_clan_rankings_clan", columnList = "clan_id"),
        Index(name = "idx_clan_rankings_tier", columnList = "ranking_tier"),
        Index(name = "idx_clan_rankings_updated", columnList = "last_updated_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_clan_rankings", columnNames = ["clan_id", "game_id"])
    ]
)
class ClanRanking(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game
) : BaseTimeEntity() {

    @Column(name = "total_score", nullable = false)
    var totalScore: Int = 0

    @Column(name = "season_score", nullable = false)
    var seasonScore: Int = 0

    @Column(name = "previous_season_score", nullable = false)
    var previousSeasonScore: Int = 0

    @Column(name = "total_matches", nullable = false)
    var totalMatches: Int = 0

    @Column(name = "wins", nullable = false)
    var wins: Int = 0

    @Column(name = "losses", nullable = false)
    var losses: Int = 0

    @Column(name = "draws", nullable = false)
    var draws: Int = 0

    // === 연속 기록 ===
    @Column(name = "win_streak", nullable = false)
    var winStreak: Int = 0

    @Column(name = "max_win_streak", nullable = false)
    var maxWinStreak: Int = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_tier", nullable = false, length = 20)
    var rankingTier: ClanRankingTier = ClanRankingTier.BRONZE

    @Column(name = "tier_points", nullable = false)
    var tierPoints: Int = 0

    @Column(name = "current_rank")
    var currentRank: Int? = null

    @Column(name = "peak_rank")
    var peakRank: Int? = null

    @Column(name = "current_season", nullable = false)
    var currentSeason: Int = 1

    @Column(name = "last_match_at")
    var lastMatchAt: LocalDateTime? = null

    @Column(name = "last_updated_at", nullable = false)
    var lastUpdatedAt: LocalDateTime = LocalDateTime.now()

    @Version
    var version: Long = 0

    fun getWinRate(): Double {
        return if (totalMatches > 0) wins.toDouble() / totalMatches else 0.0
    }

    fun isActive(): Boolean {
        return lastMatchAt?.isAfter(LocalDateTime.now().minusDays(30)) ?: false
    }
}