package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.system.enum.ReportReason
import com.rallyup.backend.domain.system.enum.ReportStatus
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "promotion_post_reports",
    indexes = [
        Index(name = "idx_promotion_post_reports_post_id", columnList = "promotion_post_id"),
        Index(name = "idx_promotion_post_reports_reporter_id", columnList = "reporter_id"),
        Index(name = "idx_promotion_post_reports_status", columnList = "status"),
        Index(name = "idx_promotion_post_reports_deleted_at", columnList = "deleted_at")
    ]
)
class PromotionPostReport(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_post_id", nullable = false)
    var promotionPost: ClanPromotionPost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    var reporter: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    var reason: ReportReason,

    @Column(name = "description", length = 1000)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    var reviewedBy: User? = null,

    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null

) : BaseTimeEntity()