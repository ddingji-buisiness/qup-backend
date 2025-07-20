package com.rallyup.backend.domain.feed.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.feed.enum.InteractionType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "feed_interactions",
    indexes = [
        Index(name = "idx_feed_interactions_user_id", columnList = "user_id"),
        Index(name = "idx_feed_interactions_feed_item_id", columnList = "feed_item_id"),
        Index(name = "idx_feed_interactions_interaction_type", columnList = "interaction_type"),
        Index(name = "idx_feed_interactions_created_at", columnList = "created_at"),
        Index(name = "idx_feed_interactions_deleted_at", columnList = "deleted_at")
    ]
)
class FeedInteraction(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_item_id", nullable = false)
    var feedItem: FeedItem,

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type")
    var interactionType: InteractionType

) : BaseTimeEntity() {

    fun isPositiveInteraction(): Boolean {
        return interactionType in listOf(
            InteractionType.LIKE,
            InteractionType.SHARE,
            InteractionType.APPLY,
            InteractionType.CLICK
        )
    }

    fun isNegativeInteraction(): Boolean {
        return interactionType in listOf(
            InteractionType.HIDE,
            InteractionType.REPORT
        )
    }

    override fun toString(): String {
        return "FeedInteraction(id=$id, userId=${user.id}, feedItemId=${feedItem.id}, type=$interactionType)"
    }
}