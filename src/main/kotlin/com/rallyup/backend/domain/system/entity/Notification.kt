package com.rallyup.backend.domain.system.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.system.enum.NotificationType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "notifications",
    indexes = [
        Index(name = "idx_notifications_user_unread", columnList = "user_id, is_read, created_at"),
        Index(name = "idx_notifications_type_created", columnList = "notification_type, created_at"),
        Index(name = "idx_notifications_scheduled", columnList = "scheduled_at, is_sent"),
        Index(name = "idx_notifications_priority", columnList = "priority, created_at"),
        Index(name = "idx_notifications_expires", columnList = "expires_at")
    ]
)
class Notification(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    var type: NotificationType,

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "message", length = 1000, nullable = false)
    var message: String
) : BaseTimeEntity() {

    @Column(name = "category", length = 30)
    var category: String? = null // "매칭", "클랜", "시스템" 등

    @Column(name = "priority", nullable = false)
    var priority: Int = 3 // 1: 긴급, 2: 높음, 3: 보통, 4: 낮음

    // === 상태 관리 ===
    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null

    @Column(name = "is_sent", nullable = false)
    var isSent: Boolean = false

    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null

    // === 스케줄링 ===
    @Column(name = "scheduled_at")
    var scheduledAt: LocalDateTime? = null

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0

    @Column(name = "max_retries", nullable = false)
    var maxRetries: Int = 3

    // === 메타데이터 ===
    @Column(name = "data", columnDefinition = "JSON")
    var data: String? = null

    @Column(name = "action_url", length = 2048)
    var actionUrl: String? = null // 클릭시 이동할 URL

    @Column(name = "action_text", length = 100)
    var actionText: String? = null // 액션 버튼 텍스트

    // === 발송 채널 ===
    @Column(name = "push_enabled", nullable = false)
    var pushEnabled: Boolean = true

    @Column(name = "email_enabled", nullable = false)
    var emailEnabled: Boolean = false

    @Column(name = "sms_enabled", nullable = false)
    var smsEnabled: Boolean = false

    @Version
    var version: Long = 0

    companion object {
        fun createImmediate(
            user: User,
            type: NotificationType,
            title: String,
            message: String,
            actionUrl: String? = null
        ): Notification {
            return Notification(user, type, title, message).apply {
                this.actionUrl = actionUrl
                categorizeByType()
                setPriorityByType()
            }
        }

        fun createScheduled(
            user: User,
            type: NotificationType,
            title: String,
            message: String,
            scheduledAt: LocalDateTime,
            expiresAt: LocalDateTime? = null
        ): Notification {
            return Notification(user, type, title, message).apply {
                this.scheduledAt = scheduledAt
                this.expiresAt = expiresAt
                categorizeByType()
                setPriorityByType()
            }
        }
    }

    private fun categorizeByType() {
        this.category = when (type) {
            NotificationType.SIGNAL_RESPONSE, NotificationType.SIGNAL_MATCHED,
            NotificationType.SESSION_STARTING -> "매칭"

            NotificationType.CLAN_APPLICATION, NotificationType.CLAN_ACCEPTED,
            NotificationType.CLAN_INVITED -> "클랜"

            NotificationType.FEEDBACK_RECEIVED, NotificationType.MANNER_SCORE_UPDATED -> "평가"

            NotificationType.SYSTEM_ANNOUNCEMENT, NotificationType.PREMIUM_EXPIRING -> "시스템"

            else -> "일반"
        }
    }

    private fun setPriorityByType() {
        this.priority = when (type) {
            NotificationType.SESSION_STARTING -> 1 // 긴급
            NotificationType.SIGNAL_MATCHED, NotificationType.CLAN_ACCEPTED -> 2 // 높음
            NotificationType.SIGNAL_RESPONSE, NotificationType.CLAN_APPLICATION -> 3 // 보통
            else -> 4 // 낮음
        }
    }

    override fun toString(): String {
        return "Notification(id=$id, userId=${user.id}, type=$type, isRead=$isRead)"
    }
}