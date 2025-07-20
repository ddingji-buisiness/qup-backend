package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.InquiryCategory
import com.rallyup.backend.domain.clan.enum.StatisticsPeriod
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "qna_analytics",
    indexes = [
        Index(name = "idx_qna_analytics_clan_date", columnList = "clan_id, analytics_date"),
        Index(name = "idx_qna_analytics_promotion", columnList = "promotion_post_id"),
        Index(name = "idx_qna_analytics_category", columnList = "inquiry_category")
    ]
)
class QnAAnalytics(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "analytics_date", nullable = false)
    var analyticsDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 10)
    var periodType: StatisticsPeriod = StatisticsPeriod.DAILY
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_post_id")
    var promotionPost: ClanPromotionPost? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", length = 20)
    var inquiryCategory: InquiryCategory? = null

    // === 기본 통계 ===
    @Column(name = "total_sessions", nullable = false)
    var totalSessions: Int = 0

    @Column(name = "active_sessions", nullable = false)
    var activeSessions: Int = 0

    @Column(name = "closed_sessions", nullable = false)
    var closedSessions: Int = 0

    @Column(name = "auto_resolved_sessions", nullable = false)
    var autoResolvedSessions: Int = 0

    // === 응답 시간 통계 ===
    @Column(name = "avg_first_response_minutes")
    var avgFirstResponseMinutes: Double? = null

    @Column(name = "avg_resolution_minutes")
    var avgResolutionMinutes: Double? = null

    // === 전환 통계 ===
    @Column(name = "converted_to_applications", nullable = false)
    var convertedToApplications: Int = 0

    @Column(name = "conversion_rate")
    var conversionRate: Double? = null

    // === 만족도 통계 ===
    @Column(name = "avg_satisfaction_rating")
    var avgSatisfactionRating: Double? = null

    @Column(name = "total_ratings", nullable = false)
    var totalRatings: Int = 0

    fun calculateConversionRate() {
        this.conversionRate = if (closedSessions > 0) {
            convertedToApplications.toDouble() / closedSessions
        } else 0.0
    }
}