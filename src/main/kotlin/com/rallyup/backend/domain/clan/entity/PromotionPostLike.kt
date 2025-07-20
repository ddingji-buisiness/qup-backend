package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "promotion_post_likes",
    indexes = [
        Index(name = "idx_promotion_post_likes_post_id", columnList = "promotion_post_id"),
        Index(name = "idx_promotion_post_likes_user_id", columnList = "user_id"),
        Index(name = "idx_promotion_post_likes_deleted_at", columnList = "deleted_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_promotion_post_likes", columnNames = ["promotion_post_id", "user_id"])
    ]
)
class PromotionPostLike(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_post_id", nullable = false)
    var promotionPost: ClanPromotionPost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : BaseTimeEntity()