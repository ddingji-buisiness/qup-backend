package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.InquiryCategory
import com.rallyup.backend.domain.clan.enum.QnASessionStatus
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_qna_sessions",
    indexes = [
        Index(name = "idx_qna_sessions_clan_status", columnList = "clan_id, status"), // 클랜별 활성 세션
        Index(name = "idx_qna_sessions_applicant", columnList = "applicant_id, status"), // 사용자별 세션
        Index(name = "idx_qna_sessions_promotion", columnList = "promotion_post_id"), // 홍보글별 문의
        Index(name = "idx_qna_sessions_activity", columnList = "last_activity_at"), // 활동 시간순
        Index(name = "idx_qna_sessions_created", columnList = "created_at"), // 생성 시간순
        Index(name = "idx_qna_sessions_auto_close", columnList = "status, last_activity_at") // 자동 종료 대상
    ]
)
class ClanQnASession(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    var applicant: User
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_post_id")
    var promotionPost: ClanPromotionPost? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: QnASessionStatus = QnASessionStatus.ACTIVE

    @Column(name = "auto_response_sent", nullable = false)
    var autoResponseSent: Boolean = false

    @Column(name = "discord_link_provided", nullable = false)
    var discordLinkProvided: Boolean = false

    @Column(name = "last_activity_at", nullable = false)
    var lastActivityAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "closed_at")
    var closedAt: LocalDateTime? = null

    // === 🔥 핵심: 연관관계 유지 (제거하면 안 됨) ===
    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var messages: MutableList<QnAMessage> = mutableListOf()
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", nullable = false, length = 20)
    var inquiryCategory: InquiryCategory = InquiryCategory.GENERAL

    @Column(name = "initial_question", length = 500)
    var initialQuestion: String? = null // 첫 질문 요약 (검색/분석용)

    @Column(name = "priority", nullable = false)
    var priority: Int = 3 // 1: 높음, 2: 보통, 3: 낮음

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_responder_id")
    var assignedResponder: User? = null // 담당자 배정

    // === 성과 측정 ===
    @Column(name = "first_response_time_minutes")
    var firstResponseTimeMinutes: Long? = null

    @Column(name = "resolution_time_minutes")
    var resolutionTimeMinutes: Long? = null

    @Column(name = "message_count", nullable = false)
    var messageCount: Int = 0

    @Column(name = "auto_resolved", nullable = false)
    var autoResolved: Boolean = false // FAQ로 해결됨

    @Column(name = "converted_to_application", nullable = false)
    var convertedToApplication: Boolean = false // 실제 지원으로 이어짐

    @Column(name = "satisfaction_rating")
    var satisfactionRating: Int? = null // 1-5점

    // === 알림 관리 ===
    @Column(name = "responder_notified", nullable = false)
    var responderNotified: Boolean = false

    @Column(name = "applicant_last_notified")
    var applicantLastNotified: LocalDateTime? = null

    @Version
    var version: Long = 0


    override fun toString(): String {
        return "ClanQnASession(id=$id, clanId=${clan.id}, applicantId=${applicant.id}, " +
                "status=$status, messageCount=$messageCount, promotionPostId=${promotionPost?.id})"
    }
}