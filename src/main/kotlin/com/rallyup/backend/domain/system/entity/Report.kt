package com.rallyup.backend.domain.system.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.system.enum.ReportReason
import com.rallyup.backend.domain.system.enum.ReportStatus
import com.rallyup.backend.domain.system.enum.ReportTargetType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "reports",
    indexes = [
        Index(name = "idx_reports_status_created", columnList = "status, created_at"),
        Index(name = "idx_reports_target", columnList = "target_type, target_id"),
        Index(name = "idx_reports_reporter", columnList = "reporter_id"),
        Index(name = "idx_reports_reason", columnList = "reason"),
        Index(name = "idx_reports_priority", columnList = "priority, created_at")
    ]
)
class Report(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    var reporter: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    var targetType: ReportTargetType,

    @Column(name = "target_id", nullable = false)
    var targetId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    var reason: ReportReason,

    @Column(name = "description", length = 1000)
    var description: String? = null
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    var reportedUser: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ReportStatus = ReportStatus.PENDING

    @Column(name = "priority", nullable = false)
    var priority: Int = 3 // 1: 긴급, 2: 높음, 3: 보통, 4: 낮음

    @Column(name = "category", length = 50)
    var category: String? = null // 자동 분류된 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    var reviewedBy: User? = null

    @Column(name = "review_note", length = 1000)
    var reviewNote: String? = null

    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null

    @Column(name = "auto_processed", nullable = false)
    var autoProcessed: Boolean = false

    @Column(name = "evidence_urls", columnDefinition = "JSON")
    var evidenceUrls: String? = null

    @Column(name = "evidence_count", nullable = false)
    var evidenceCount: Int = 0

    @Version
    var version: Long = 0

    companion object {
        fun create(
            reporter: User,
            targetType: ReportTargetType,
            targetId: Long,
            reason: ReportReason,
            description: String? = null,
            reportedUser: User? = null
        ): Report {
            return Report(reporter, targetType, targetId, reason, description).apply {
                this.reportedUser = reportedUser
                calculatePriority()
                categorizeAutomatically()
            }
        }
    }

    private fun calculatePriority() {
        this.priority = when (reason) {
            ReportReason.HARASSMENT, ReportReason.INAPPROPRIATE_CONTENT -> 1 // 긴급
            ReportReason.TOXICITY, ReportReason.CHEATING -> 2 // 높음
            ReportReason.SPAM, ReportReason.FAKE_INFORMATION -> 3 // 보통
            else -> 4 // 낮음
        }
    }

    private fun categorizeAutomatically() {
        this.category = when (targetType) {
            ReportTargetType.USER -> "사용자 신고"
            ReportTargetType.CLAN -> "클랜 신고"
            ReportTargetType.MESSAGE -> "메시지 신고"
            else -> "기타 신고"
        }
    }

    override fun toString(): String {
        return "Report(id=$id, targetType=$targetType, reason=$reason, status=$status)"
    }
}