package com.rallyup.backend.domain.game.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "gambti_results",
    indexes = [
        Index(name = "idx_gambti_user_current", columnList = "user_id, is_current"),
        Index(name = "idx_gambti_result_code", columnList = "result_code"),
        Index(name = "idx_gambti_taken_at", columnList = "taken_at")
    ]
)
class GamBTIResult(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "test_type", length = 50, nullable = false)
    var testType: String,

    @Column(name = "result_code", length = 10, nullable = false)
    var resultCode: String,

    @Column(name = "result_name", length = 100, nullable = false)
    var resultName: String
) : BaseTimeEntity() {

    @Column(name = "result_description", length = 1000)
    var resultDescription: String? = null

    @Column(name = "badge_emoji", length = 10)
    var badgeEmoji: String? = null

    @Column(name = "score_data", columnDefinition = "JSON")
    var scoreData: String? = null

    @Column(name = "is_current", nullable = false)
    var isCurrent: Boolean = true

    @Column(name = "test_version", length = 10, nullable = false)
    var testVersion: String = "1.0"

    @Column(name = "taken_at", nullable = false)
    var takenAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "validity_period_days", nullable = false)
    var validityPeriodDays: Int = 365

    @Column(name = "confidence_score")
    var confidenceScore: Double? = null

    init {
        require(resultCode.length in 2..10) { "결과 코드는 2-10자 사이여야 합니다." }
        require(validityPeriodDays > 0) { "유효 기간은 1일 이상이어야 합니다." }
        confidenceScore?.let { require(it in 0.0..100.0) { "신뢰도는 0-100 사이여야 합니다." } }
    }

    override fun toString(): String {
        return "GamBTIResult(id=$id, userId=${user.id}, resultCode='$resultCode', " +
                "testType='$testType', isCurrent=$isCurrent, takenAt=$takenAt)"
    }
}