package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "response_assessments",
    indexes = [
        Index(name = "idx_response_assessments_score", columnList = "compatibility_score"),
        Index(name = "idx_response_assessments_priority", columnList = "processing_priority"),
        Index(name = "idx_response_assessments_auto_eligible", columnList = "auto_review_eligible")
    ]
)
class ResponseAssessment(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    var response: SignalResponse
) : BaseTimeEntity() {

    @Column(name = "compatibility_score")
    var compatibilityScore: Double? = null

    @Column(name = "response_quality_score")
    var responseQualityScore: Double? = null

    @Column(name = "profile_completeness")
    var profileCompleteness: Double? = null

    @Column(name = "processing_priority", nullable = false)
    var processingPriority: Int = 50

    @Column(name = "auto_review_eligible", nullable = false)
    var autoReviewEligible: Boolean = false

    @Column(name = "manual_review_required", nullable = false)
    var manualReviewRequired: Boolean = false

    @Column(name = "review_reason", length = 200)
    var reviewReason: String? = null

    @Column(name = "response_time_seconds")
    var responseTimeSeconds: Long? = null

    @Column(name = "processing_deadline")
    var processingDeadline: LocalDateTime? = null

    init {
        require(processingPriority in 0..100) { "처리 우선순위는 0-100 사이여야 합니다." }
        compatibilityScore?.let { require(it in 0.0..100.0) { "호환성 점수는 0-100 사이여야 합니다." } }
        responseQualityScore?.let { require(it in 0.0..100.0) { "응답 품질 점수는 0-100 사이여야 합니다." } }
        profileCompleteness?.let { require(it in 0.0..100.0) { "프로필 완성도는 0-100 사이여야 합니다." } }
    }
}