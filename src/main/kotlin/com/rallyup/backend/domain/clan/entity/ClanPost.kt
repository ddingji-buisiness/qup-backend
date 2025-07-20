package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.PostStatus
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_posts",
    indexes = [
        Index(name = "idx_clan_posts_clan_status", columnList = "clan_id, status"),
        Index(name = "idx_clan_posts_game_featured", columnList = "game_id, is_featured, created_at"),
        Index(name = "idx_clan_posts_status_bump", columnList = "status, last_bumped_at"),
        Index(name = "idx_clan_posts_expires", columnList = "expires_at"),
        Index(name = "idx_clan_posts_engagement", columnList = "like_count, view_count")
    ]
)
class ClanPost(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "content", length = 3000, nullable = false)
    var content: String
) : BaseTimeEntity() {

    @Column(name = "short_description", length = 200)
    var shortDescription: String? = null

    @Column(name = "image_urls", columnDefinition = "JSON")
    var imageUrls: String? = null

    @Column(name = "tags", columnDefinition = "JSON")
    var tags: String? = null // ["클린", "친목", "경쟁"]

    @Column(name = "contact_info", length = 500)
    var contactInfo: String? = null // Discord 링크, 연락처 등

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: PostStatus = PostStatus.PENDING

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime? = LocalDateTime.now().plusDays(DEFAULT_DURATION_DAYS)

    @Column(name = "is_featured", nullable = false)
    var isFeatured: Boolean = false

    @Column(name = "featured_until")
    var featuredUntil: LocalDateTime? = null

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0

    @Column(name = "application_count", nullable = false)
    var applicationCount: Long = 0

    @Column(name = "last_bumped_at")
    var lastBumpedAt: LocalDateTime? = null

    @Column(name = "daily_bump_count", nullable = false)
    var dailyBumpCount: Int = 0

    @Column(name = "bump_date")
    var bumpDate: LocalDate? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    var reviewedBy: User? = null

    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null

    @Column(name = "review_note", length = 500)
    var reviewNote: String? = null

    @Version
    var version: Long = 0

    companion object {
        const val MAX_DAILY_BUMPS = 1
        const val DEFAULT_DURATION_DAYS = 30L
    }

    fun approve(reviewer: User, note: String? = null) {
        this.status = PostStatus.APPROVED
        this.reviewedBy = reviewer
        this.reviewedAt = LocalDateTime.now()
        this.reviewNote = note
    }

    fun reject(reviewer: User, note: String) {
        this.status = PostStatus.REJECTED
        this.reviewedBy = reviewer
        this.reviewedAt = LocalDateTime.now()
        this.reviewNote = note
    }

    fun bump(): Boolean {
        val today = LocalDate.now()

        // 일일 끌올 제한 체크
        if (bumpDate != today) {
            bumpDate = today
            dailyBumpCount = 0
        }

        if (dailyBumpCount >= MAX_DAILY_BUMPS) {
            return false
        }

        this.lastBumpedAt = LocalDateTime.now()
        this.dailyBumpCount++
        return true
    }

    fun feature(durationDays: Long) {
        this.isFeatured = true
        this.featuredUntil = LocalDateTime.now().plusDays(durationDays)
    }

    fun incrementView() {
        this.viewCount++
    }

    fun incrementApplication() {
        this.applicationCount++
    }

    fun isActive(): Boolean = status == PostStatus.APPROVED && !isExpired()
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    fun isFeaturedNow(): Boolean = isFeatured && featuredUntil?.isAfter(LocalDateTime.now()) == true
    fun canBump(): Boolean = bumpDate != LocalDate.now() || dailyBumpCount < MAX_DAILY_BUMPS

    fun getEngagementScore(): Double {
        val viewWeight = 1.0
        val likeWeight = 10.0
        val applicationWeight = 50.0

        return viewCount * viewWeight + likeCount * likeWeight + applicationCount * applicationWeight
    }
}