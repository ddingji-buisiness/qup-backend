package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.matching.enum.FeedbackCategory
import com.rallyup.backend.domain.matching.enum.FeedbackType
import com.rallyup.backend.domain.matching.enum.SourcePlatform
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "matching_feedbacks",
    indexes = [
        Index(name = "idx_matching_feedbacks_session", columnList = "session_id"),
        Index(name = "idx_matching_feedbacks_reviewer", columnList = "reviewer_id"),
        Index(name = "idx_matching_feedbacks_reviewee", columnList = "reviewee_id"),
        Index(name = "idx_matching_feedbacks_rating", columnList = "rating"),
        Index(name = "idx_matching_feedbacks_mutual", columnList = "is_mutual")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_matching_feedback", columnNames = ["session_id", "reviewer_id", "reviewee_id"])
    ]
)
class MatchingFeedback(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: MatchingSession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    var reviewer: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    var reviewee: User,

    @Column(name = "rating", nullable = false)
    var rating: Int
) : BaseTimeEntity() {

    @Column(name = "tags", columnDefinition = "JSON")
    var tags: String? = null

    @Column(name = "comment", length = 500)
    var comment: String? = null

    @Column(name = "is_positive", nullable = false)
    var isPositive: Boolean = true

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 20)
    var feedbackType: FeedbackType = FeedbackType.GENERAL

    @Column(name = "would_play_again", nullable = false)
    var wouldPlayAgain: Boolean = true

    @Column(name = "recommended_for_friends", nullable = false)
    var recommendedForFriends: Boolean = false

    @Column(name = "is_anonymous", nullable = false)
    var isAnonymous: Boolean = false

    @Column(name = "is_mutual", nullable = false)
    var isMutual: Boolean = false

    @Column(name = "mutual_feedback_id")
    var mutualFeedbackId: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "source_platform", nullable = false, length = 20)
    var sourcePlatform: SourcePlatform = SourcePlatform.WEB

    @Version
    var version: Long = 0

    init {
        require(rating in 1..5) { "평점은 1-5 사이여야 합니다." }
        require(reviewer.id != reviewee.id) { "자기 자신에게 피드백을 줄 수 없습니다." }
    }
}