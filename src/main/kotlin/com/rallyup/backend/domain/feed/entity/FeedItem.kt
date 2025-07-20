package com.rallyup.backend.domain.feed.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.feed.enum.FeedItemType
import com.rallyup.backend.domain.feed.enum.InteractionType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "feed_items",
    indexes = [
        Index(name = "idx_feed_items_item_type", columnList = "item_type"),
        Index(name = "idx_feed_items_item_id", columnList = "item_id"),
        Index(name = "idx_feed_items_priority_score", columnList = "priority_score"),
        Index(name = "idx_feed_items_is_sponsored", columnList = "is_sponsored"),
        Index(name = "idx_feed_items_expires_at", columnList = "expires_at"),
        Index(name = "idx_feed_items_deleted_at", columnList = "deleted_at")
    ]
)
class FeedItem(
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    var itemType: FeedItemType,

    @Column(name = "item_id")
    var itemId: Long,

    @Column(name = "title", length = 200)
    var title: String,

    @Column(name = "content", length = 1000)
    var content: String? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "target_audience", columnDefinition = "JSON")
    var targetAudience: String? = null,

    @Column(name = "priority_score")
    var priorityScore: Double = 0.0,

    @Column(name = "is_sponsored")
    var isSponsored: Boolean = false,

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null

) : BaseTimeEntity() {

    @OneToMany(mappedBy = "feedItem", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var interactions: MutableList<FeedInteraction> = mutableListOf()
        protected set

    fun updateContent(title: String, content: String?, imageUrl: String?) {
        this.title = title
        this.content = content
        this.imageUrl = imageUrl
    }

    fun updatePriorityScore(score: Double) {
        this.priorityScore = score
    }

    fun sponsorItem(expiresAt: LocalDateTime) {
        this.isSponsored = true
        this.expiresAt = expiresAt
        this.priorityScore += 50.0 // 스폰서 보너스
    }

    fun isExpired(): Boolean {
        return expiresAt?.isBefore(LocalDateTime.now()) ?: false
    }

    fun isActive(): Boolean {
        return !isExpired() && !isDeleted()
    }

    fun addInteraction(user: User, interactionType: InteractionType): FeedInteraction {
        val interaction = FeedInteraction(
            user = user,
            feedItem = this,
            interactionType = interactionType
        )
        interactions.add(interaction)
        return interaction
    }

    fun getInteractionCount(type: InteractionType): Long {
        return interactions.count { it.interactionType == type }.toLong()
    }

    fun getClickThroughRate(): Double {
        val views = getInteractionCount(InteractionType.VIEW)
        val clicks = getInteractionCount(InteractionType.CLICK)
        return if (views > 0) (clicks.toDouble() / views) * 100 else 0.0
    }

    override fun toString(): String {
        return "FeedItem(id=$id, itemType=$itemType, title='$title', isSponsored=$isSponsored)"
    }
}