package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ClanMatchType
import com.rallyup.backend.domain.clan.enum.MatchVerificationStatus
import com.rallyup.backend.domain.clan.enum.MatchVerificationStatus.*
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.matching.entity.MatchingSession
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_match_results",
    indexes = [
        Index(name = "idx_clan_match_results_clan_game", columnList = "clan_id, game_id, match_date"), // 클랜별 게임별 조회
        Index(name = "idx_clan_match_results_opponent", columnList = "opponent_clan_id, match_date"), // 상대 클랜별 조회
        Index(name = "idx_clan_match_results_session", columnList = "session_id"), // 세션별 조회
        Index(name = "idx_clan_match_results_verification", columnList = "verification_status") // 검증 상태별 조회
    ]
)
class ClanMatchResult(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: MatchingSession,

    @Column(name = "is_victory", nullable = false)
    var isVictory: Boolean,

    @Column(name = "match_date", nullable = false)
    var matchDate: LocalDateTime = LocalDateTime.now()

) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_clan_id")
    var opponentClan: Clan? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 20)
    var matchType: ClanMatchType = ClanMatchType.FRIENDLY

    @Column(name = "team_score")
    var teamScore: Int? = null

    @Column(name = "opponent_score")
    var opponentScore: Int? = null

    @Column(name = "match_duration_minutes")
    var matchDurationMinutes: Int? = null

    @Column(name = "mvp_user_id")
    var mvpUserId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    var verificationStatus: MatchVerificationStatus = PENDING

    @Column(name = "evidence_urls", columnDefinition = "JSON")
    var evidenceUrls: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id", nullable = false)
    var submittedBy: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    var verifiedBy: User? = null

    @Column(name = "verified_at")
    var verifiedAt: LocalDateTime? = null

    @Column(name = "points_earned", nullable = false)
    var pointsEarned: Int = 0

    @Column(name = "ranking_impact", nullable = false)
    var rankingImpact: Int = 0

    @Version
    var version: Long = 0

    init {
        calculatePoints()
    }

    private fun calculatePoints() {
        val basePoints = when (matchType) {
            ClanMatchType.RANKED -> 50
            ClanMatchType.TOURNAMENT -> 100
            ClanMatchType.FRIENDLY -> 20
            ClanMatchType.PRACTICE -> 10
        }

        val victoryMultiplier = if (isVictory) 1.0 else 0.3
        val evidenceBonus = if (!evidenceUrls.isNullOrBlank()) 1.2 else 1.0

        this.pointsEarned = (basePoints * victoryMultiplier * evidenceBonus).toInt()
        this.rankingImpact = if (isVictory) pointsEarned else -pointsEarned / 2
    }

    fun verify(verifier: User) {
        this.verificationStatus = VERIFIED
        this.verifiedBy = verifier
        this.verifiedAt = LocalDateTime.now()
    }

    fun reject(verifier: User, reason: String? = null) {
        this.verificationStatus = REJECTED
        this.verifiedBy = verifier
        this.verifiedAt = LocalDateTime.now()
        this.pointsEarned = 0
        this.rankingImpact = 0
    }
}