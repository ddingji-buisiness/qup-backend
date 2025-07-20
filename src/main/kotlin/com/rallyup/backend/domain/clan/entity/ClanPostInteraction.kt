package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.feed.enum.InteractionType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "clan_post_interactions",
    indexes = [
        Index(name = "idx_clan_post_interactions_post", columnList = "post_id, interaction_type"), // 포스트별 상호작용
        Index(name = "idx_clan_post_interactions_user", columnList = "user_id, interaction_type"), // 사용자별 상호작용
        Index(name = "idx_clan_post_interactions_created", columnList = "created_at") // 시간순
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_post_user_interaction", columnNames = ["post_id", "user_id", "interaction_type"])
    ]
)
class ClanPostInteraction(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: ClanPost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false, length = 20)
    var interactionType: InteractionType
) : BaseTimeEntity() {

    @Column(name = "note", length = 500)
    var note: String? = null // 신고 사유 등

    companion object {
        fun like(post: ClanPost, user: User): ClanPostInteraction {
            return ClanPostInteraction(post, user, InteractionType.LIKE)
        }

        fun report(post: ClanPost, user: User, reason: String): ClanPostInteraction {
            return ClanPostInteraction(post, user, InteractionType.REPORT).apply {
                this.note = reason
            }
        }

        fun view(post: ClanPost, user: User): ClanPostInteraction {
            return ClanPostInteraction(post, user, InteractionType.VIEW)
        }

        fun apply(post: ClanPost, user: User): ClanPostInteraction {
            return ClanPostInteraction(post, user, InteractionType.APPLICATION)
        }
    }
}