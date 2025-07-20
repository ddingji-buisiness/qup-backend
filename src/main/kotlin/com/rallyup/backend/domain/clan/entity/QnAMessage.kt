package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "qna_messages",
    indexes = [
        Index(name = "idx_qna_messages_session_created", columnList = "session_id, created_at"),
        Index(name = "idx_qna_messages_sender", columnList = "sender_id"),
        Index(name = "idx_qna_messages_auto_response", columnList = "is_auto_response"),
        Index(name = "idx_qna_messages_unread", columnList = "session_id, is_read")
    ]
)
class QnAMessage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: ClanQnASession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User,

    @Column(name = "content", length = 2000, nullable = false)
    var content: String,

    @Column(name = "is_auto_response", nullable = false)
    var isAutoResponse: Boolean = false
) : BaseTimeEntity() {

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null

    // === 개선사항: 추가 메타데이터 ===
    @Column(name = "message_sequence", nullable = false)
    var messageSequence: Int = 0 // 세션 내 메시지 순서

    @Column(name = "contains_contact_info", nullable = false)
    var containsContactInfo: Boolean = false // 연락처 정보 포함 여부

    @Column(name = "sentiment_score")
    var sentimentScore: Double? = null // 감정 분석 점수 (-1.0 ~ 1.0)

    @Column(name = "response_type", length = 50)
    var responseType: String? = null // "faq", "custom", "discord_invite" 등

    override fun toString(): String {
        return "QnAMessage(id=$id, sessionId=${session.id}, senderId=${sender.id}, isAutoResponse=$isAutoResponse)"
    }
}