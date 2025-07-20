package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.RecommendationType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_recommendations",
    indexes = [
        Index(name = "idx_clan_recommendations_user", columnList = "user_id, created_at"), // 사용자별 추천
        Index(name = "idx_clan_recommendations_clan", columnList = "recommended_clan_id"), // 추천된 클랜별
        Index(name = "idx_clan_recommendations_score", columnList = "compatibility_score"), // 점수별
        Index(name = "idx_clan_recommendations_expires", columnList = "expires_at") // 만료 관리
    ]
)
class ClanRecommendation(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_clan_id", nullable = false)
    var recommendedClan: Clan,

    @Column(name = "compatibility_score", nullable = false)
    var compatibilityScore: Double,

    @Column(name = "recommendation_reason", length = 500)
    var recommendationReason: String
) : BaseTimeEntity() {

    @Column(name = "is_viewed", nullable = false)
    var isViewed: Boolean = false

    @Column(name = "viewed_at")
    var viewedAt: LocalDateTime? = null

    @Column(name = "is_applied", nullable = false)
    var isApplied: Boolean = false

    @Column(name = "applied_at")
    var appliedAt: LocalDateTime? = null

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now().plusDays(7)

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false, length = 20)
    var recommendationType: RecommendationType = RecommendationType.ALGORITHM
}