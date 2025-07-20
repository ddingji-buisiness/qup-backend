package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_message_read_status",
    indexes = [
        Index(name = "idx_message_read_user_channel", columnList = "user_id, channel_id"), // 사용자별 채널별 읽음 상태
        Index(name = "idx_message_read_channel_message", columnList = "channel_id, last_read_message_id"), // 채널별 읽음 상태
        Index(name = "idx_message_read_updated", columnList = "updated_at") // 최근 활동
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_message_read_status", columnNames = ["user_id", "channel_id"])
    ]
)
class ClanMessageReadStatus(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    var channel: ClanChannel
) : BaseTimeEntity() {

    @Column(name = "last_read_message_id")
    var lastReadMessageId: Long? = null

    @Column(name = "last_read_at")
    var lastReadAt: LocalDateTime? = null

    @Column(name = "unread_count", nullable = false)
    var unreadCount: Int = 0

    @Column(name = "mention_count", nullable = false)
    var mentionCount: Int = 0 // 읽지 않은 멘션 수

    fun markAsRead(messageId: Long) {
        this.lastReadMessageId = messageId
        this.lastReadAt = LocalDateTime.now()
        this.unreadCount = 0
        this.mentionCount = 0
    }

    fun incrementUnread(hasMention: Boolean = false) {
        this.unreadCount++
        if (hasMention) {
            this.mentionCount++
        }
    }
}