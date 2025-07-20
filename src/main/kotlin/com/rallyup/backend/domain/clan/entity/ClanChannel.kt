package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ChannelType
import com.rallyup.backend.domain.clan.enum.ClanRole
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_channels",
    indexes = [
        Index(name = "idx_clan_channels_clan_type", columnList = "clan_id, channel_type"), // 클랜별 채널 타입
        Index(name = "idx_clan_channels_game", columnList = "game_id"), // 게임별 채널
        Index(name = "idx_clan_channels_active", columnList = "is_active"), // 활성 채널
        Index(name = "idx_clan_channels_sort", columnList = "clan_id, sort_order") // 정렬순
    ]
)
class ClanChannel(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "name", length = 100, nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    var channelType: ChannelType
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    var game: Game? = null

    @Column(name = "description", length = 500)
    var description: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0

    @Column(name = "min_permission_level", nullable = false)
    var minPermissionLevel: Int = 0

    @Column(name = "read_only", nullable = false)
    var readOnly: Boolean = false

    @Column(name = "message_count", nullable = false)
    var messageCount: Long = 0

    @Column(name = "last_message_at")
    var lastMessageAt: LocalDateTime? = null

    @Column(name = "last_message_preview", length = 100)
    var lastMessagePreview: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_user_id")
    var lastMessageUser: User? = null

    @Version
    var version: Long = 0

    fun canUserRead(userPermissionLevel: Int): Boolean {
        return userPermissionLevel >= minPermissionLevel
    }

    fun canUserWrite(userPermissionLevel: Int): Boolean {
        return canUserRead(userPermissionLevel) && !readOnly
    }

    fun updateLastMessage(user: User, messagePreview: String) {
        this.messageCount++
        this.lastMessageAt = LocalDateTime.now()
        this.lastMessagePreview = messagePreview.take(100)
        this.lastMessageUser = user
    }
}