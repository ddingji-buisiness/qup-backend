package com.rallyup.backend.domain.feed.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "feeds",
    indexes = [
        Index(name = "idx_feeds_user_id", columnList = "user_id"),
        Index(name = "idx_feeds_last_updated", columnList = "last_updated"),
        Index(name = "idx_feeds_deleted_at", columnList = "deleted_at")
    ]
)
class Feed(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "feed_data", columnDefinition = "JSON")
    var feedData: String,

    @Column(name = "last_updated")
    var lastUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "cache_version")
    var cacheVersion: Int = 1

) : BaseTimeEntity() {

    fun updateFeedData(feedData: String) {
        this.feedData = feedData
        this.lastUpdated = LocalDateTime.now()
        this.cacheVersion++
    }

    fun invalidateCache() {
        this.cacheVersion++
        this.lastUpdated = LocalDateTime.now()
    }

    fun isStale(maxAgeMinutes: Long = 30): Boolean {
        return lastUpdated.isBefore(LocalDateTime.now().minusMinutes(maxAgeMinutes))
    }

    override fun toString(): String {
        return "Feed(id=$id, userId=${user.id}, cacheVersion=$cacheVersion, lastUpdated=$lastUpdated)"
    }
}
