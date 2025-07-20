package com.rallyup.backend.domain.game.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.game.enum.SkillLevel
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_game_infos",
    indexes = [
        Index(name = "idx_user_game_user_game", columnList = "user_id, game_id"), // 기본 조회
        Index(name = "idx_user_game_tier_verified", columnList = "game_id, current_tier_id, is_verified"), // 티어별 조회
        Index(name = "idx_user_game_position", columnList = "game_id, main_position_id"), // 포지션별 조회
        Index(name = "idx_user_game_activity", columnList = "user_id, last_played_at") // 활동성 조회
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_game_info", columnNames = ["user_id", "game_id"])
    ]
)
class UserGameInfo(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game
) : BaseTimeEntity() {

    @Column(name = "game_nickname", length = 100)
    var gameNickname: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_tier_id")
    var currentTier: GameTier? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peak_tier_id")
    var peakTier: GameTier? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_position_id")
    var mainPosition: GamePosition? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_position_id")
    var subPosition: GamePosition? = null

    @Column(name = "play_style", length = 200)
    var playStyle: String? = null

    @Column(name = "total_playtime")
    var totalPlaytime: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    var skillLevel: SkillLevel = SkillLevel.BEGINNER

    @Column(name = "last_played_at")
    var lastPlayedAt: LocalDateTime? = null

    @Column(name = "matches_played", nullable = false)
    var matchesPlayed: Int = 0

    @Column(name = "wins", nullable = false)
    var wins: Int = 0

    @Column(name = "losses", nullable = false)
    var losses: Int = 0

    @Column(name = "is_verified", nullable = false)
    var isVerified: Boolean = false

    @Column(name = "verified_at")
    var verifiedAt: LocalDateTime? = null

    @Column(name = "verification_source", length = 100)
    var verificationSource: String? = null

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = true

    @Column(name = "notes", length = 1000)
    var notes: String? = null

    @Version
    var version: Long = 0

    init {
        require(totalPlaytime == null || totalPlaytime!! >= 0) { "플레이타임은 0 이상이어야 합니다." }
        require(matchesPlayed >= 0) { "매치 수는 0 이상이어야 합니다." }
        require(wins >= 0 && losses >= 0) { "승패는 0 이상이어야 합니다." }
        require(wins + losses <= matchesPlayed) { "승패 합은 전체 매치 수를 초과할 수 없습니다." }
    }

    override fun toString(): String {
        return "UserGameInfo(id=$id, userId=${user.id}, gameId=${game.id}, " +
                "currentTier='$currentTier', skillLevel=$skillLevel, verified=$isVerified)"
    }
}