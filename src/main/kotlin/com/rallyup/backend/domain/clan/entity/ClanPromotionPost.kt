package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.PromotionPostStatus
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_promotion_posts",
    indexes = [
        Index(name = "idx_clan_promotion_posts_clan_id", columnList = "clan_id"),
        Index(name = "idx_clan_promotion_posts_game_id", columnList = "game_id"),
        Index(name = "idx_clan_promotion_posts_status", columnList = "status"),
        Index(name = "idx_clan_promotion_posts_is_featured", columnList = "is_featured"),
        Index(name = "idx_clan_promotion_posts_last_bumped", columnList = "last_bumped_at"),
        Index(name = "idx_clan_promotion_posts_view_count", columnList = "view_count"),
        Index(name = "idx_clan_promotion_posts_deleted_at", columnList = "deleted_at")
    ]
)
class ClanPromotionPost(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User, // 작성자 (클랜장/임원)

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "content", length = 5000, nullable = false)
    var content: String,

    @Column(name = "short_description", length = 200)
    var shortDescription: String? = null,

    @Column(name = "image_urls", columnDefinition = "JSON")
    var imageUrls: String? = null, // 이미지 URL 배열

    @Column(name = "tags", columnDefinition = "JSON")
    var tags: String? = null, // 태그 배열 ["클린", "친목", "경쟁"]

    @Column(name = "discord_invite_link")
    var discordInviteLink: String? = null,

    @Column(name = "auto_join_link")
    var autoJoinLink: String? = null, // 자동 가입 링크

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PromotionPostStatus = PromotionPostStatus.PENDING,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @Column(name = "application_count", nullable = false)
    var applicationCount: Long = 0,

    @Column(name = "is_featured", nullable = false)
    var isFeatured: Boolean = false, // 상단 고정

    @Column(name = "featured_until")
    var featuredUntil: LocalDateTime? = null,

    @Column(name = "last_bumped_at")
    var lastBumpedAt: LocalDateTime? = null, // 마지막 끌올 시간

    @Column(name = "bump_count")
    var bumpCount: Int = 0, // 오늘 끌올 횟수

    @Column(name = "bump_date")
    var bumpDate: LocalDate? = null, // 끌올 날짜 추적

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    var reviewedBy: User? = null, // 검수자

    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null,

    @Column(name = "review_note", length = 500)
    var reviewNote: String? = null

) : BaseTimeEntity() {

    // 연관관계
    @OneToMany(mappedBy = "promotionPost", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reports: MutableList<PromotionPostReport> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "promotionPost", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var likes: MutableList<PromotionPostLike> = mutableListOf()
        protected set

    // 비즈니스 메서드
    fun approve(reviewer: User, note: String? = null) {
        this.status = PromotionPostStatus.APPROVED
        this.reviewedBy = reviewer
        this.reviewedAt = LocalDateTime.now()
        this.reviewNote = note
    }

    fun reject(reviewer: User, note: String) {
        this.status = PromotionPostStatus.REJECTED
        this.reviewedBy = reviewer
        this.reviewedAt = LocalDateTime.now()
        this.reviewNote = note
    }

    fun bump(): Boolean {
        val today = LocalDate.now()

        // 오늘 첫 끌올이면 카운트 리셋
        if (bumpDate != today) {
            bumpDate = today
            bumpCount = 0
        }

        // 하루 1회 제한 확인
        if (bumpCount >= 1) {
            return false
        }

        this.lastBumpedAt = LocalDateTime.now()
        this.bumpCount++
        return true
    }

    fun increaseViewCount() {
        this.viewCount++
    }

    fun increaseApplicationCount() {
        this.applicationCount++
    }

    fun addLike(user: User): PromotionPostLike? {
        // 중복 좋아요 방지
        if (likes.any { it.user.id == user.id }) {
            return null
        }

        val like = PromotionPostLike(promotionPost = this, user = user)
        likes.add(like)
        likeCount++
        return like
    }

    fun removeLike(user: User): Boolean {
        val like = likes.find { it.user.id == user.id }
        return if (like != null) {
            likes.remove(like)
            likeCount = maxOf(0, likeCount - 1)
            true
        } else false
    }

    fun feature(duration: Long) {
        this.isFeatured = true
        this.featuredUntil = LocalDateTime.now().plusDays(duration)
    }

    fun unfeature() {
        this.isFeatured = false
        this.featuredUntil = null
    }

    fun isActive(): Boolean {
        return status == PromotionPostStatus.APPROVED && !isDeleted()
    }

    fun canBump(): Boolean {
        val today = LocalDate.now()
        return bumpDate != today || bumpCount < 1
    }

    override fun toString(): String {
        return "ClanPromotionPost(id=$id, clanId=${clan.id}, title='$title', status=$status)"
    }
}