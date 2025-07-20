package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ActivityType
import com.rallyup.backend.domain.clan.enum.VerificationStatus
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_activities",
    indexes = [
        Index(name = "idx_clan_activities_clan_type", columnList = "clan_id, activity_type"),
        Index(name = "idx_clan_activities_game", columnList = "game_id, created_at"),
        Index(name = "idx_clan_activities_verification", columnList = "verification_status")
    ]
)
class ClanActivity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    var activityType: ActivityType,

    @Column(name = "title", length = 200, nullable = false)
    var title: String
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    var game: Game? = null

    @Column(name = "description", length = 1000)
    var description: String? = null

    @Column(name = "image_urls", columnDefinition = "JSON")
    var imageUrls: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_clan_id")
    var opponentClan: Clan? = null

    @Column(name = "is_victory")
    var isVictory: Boolean? = null

    @Column(name = "points_earned")
    var pointsEarned: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    var verificationStatus: VerificationStatus = VerificationStatus.PENDING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    var verifiedBy: User? = null

    @Column(name = "verified_at")
    var verifiedAt: LocalDateTime? = null

    fun verify(verifier: User) {
        this.verificationStatus = VerificationStatus.VERIFIED
        this.verifiedBy = verifier
        this.verifiedAt = LocalDateTime.now()

        // 점수 지급 로직
        if (activityType == ActivityType.MATCH_RESULT && pointsEarned != null) {
            // ClanRanking 업데이트는 서비스 레이어에서 처리
        }
    }

    fun hasEvidence(): Boolean = !imageUrls.isNullOrBlank()
}