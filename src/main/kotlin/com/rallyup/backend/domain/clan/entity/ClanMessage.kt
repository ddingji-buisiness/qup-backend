package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.MessageType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_messages",
    indexes = [
        Index(name = "idx_clan_messages_channel_created", columnList = "channel_id, created_at"), // 채널별 시간순
        Index(name = "idx_clan_messages_sender", columnList = "sender_id"), // 발신자별
        Index(name = "idx_clan_messages_type", columnList = "message_type"), // 타입별
        Index(name = "idx_clan_messages_thread", columnList = "thread_root_id, created_at"), // 스레드
        Index(name = "idx_clan_messages_mentions", columnList = "has_mentions") // 멘션 포함 메시지
    ]
)
class ClanMessage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    var channel: ClanChannel,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User,

    @Column(name = "content", length = 2000, nullable = false)
    var content: String
) : BaseTimeEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    var messageType: MessageType = MessageType.TEXT

    // === 스레드 지원 ===
    @Column(name = "thread_root_id")
    var threadRootId: Long? = null // 스레드의 루트 메시지 ID

    @Column(name = "reply_count", nullable = false)
    var replyCount: Int = 0 // 이 메시지에 대한 답글 수

    // === 메타데이터 ===
    @Column(name = "has_mentions", nullable = false)
    var hasMentions: Boolean = false

    @Column(name = "mentioned_users", columnDefinition = "JSON")
    var mentionedUsers: String? = null // 멘션된 사용자 ID 배열

    @Column(name = "has_attachments", nullable = false)
    var hasAttachments: Boolean = false

    @Column(name = "attachment_urls", columnDefinition = "JSON")
    var attachmentUrls: String? = null

    // === 편집/삭제 관리 ===
    @Column(name = "is_edited", nullable = false)
    var isEdited: Boolean = false

    @Column(name = "edited_at")
    var editedAt: LocalDateTime? = null

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false

    @Column(name = "reaction_summary", columnDefinition = "JSON")
    var reactionSummary: String? = null

    @Version
    var version: Long = 0

    init {
        require(content.length <= 2000) { "메시지는 2000자를 초과할 수 없습니다." }
        processMentions()
    }

    private fun processMentions() {
        // @username 패턴 찾기
        val mentionPattern = Regex("@([\\w]+)")
        val mentions = mentionPattern.findAll(content)

        if (mentions.any()) {
            this.hasMentions = true
        }
    }

    fun edit(newContent: String) {
        require(newContent.length <= 2000) { "메시지는 2000자를 초과할 수 없습니다." }
        require(!isDeleted) { "삭제된 메시지는 편집할 수 없습니다." }

        this.content = newContent
        this.isEdited = true
        this.editedAt = LocalDateTime.now()
        processMentions()
    }

    fun softDelete() {
        this.isDeleted = true
        this.deletedAt = LocalDateTime.now()
        this.content = "[삭제된 메시지]"
    }

    fun addReply() {
        this.replyCount++
    }

    fun isThreadRoot(): Boolean = threadRootId == null && replyCount > 0
    fun isThreadReply(): Boolean = threadRootId != null
    fun getContentPreview(): String = content.take(100)
}