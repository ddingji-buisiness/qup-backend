package com.rallyup.backend.domain.system.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.system.enum.RelationshipType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "user_relationships",
    indexes = [
        Index(name = "idx_user_relationships_user_type", columnList = "user_id, relationship_type"),
        Index(name = "idx_user_relationships_target", columnList = "target_user_id"),
        Index(name = "idx_user_relationships_created", columnList = "created_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_relationship", columnNames = ["user_id", "target_user_id"])
    ]
)
class UserRelationship(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    var targetUser: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 20)
    var relationshipType: RelationshipType
) : BaseTimeEntity() {

    @Column(name = "note", length = 200)
    var note: String? = null

    @Column(name = "is_mutual", nullable = false)
    var isMutual: Boolean = false

    init {
        require(user.id != targetUser.id) { "자기 자신과는 관계를 설정할 수 없습니다." }
    }

    override fun toString(): String {
        return "UserRelationship(id=$id, userId=${user.id}, targetUserId=${targetUser.id}, type=$relationshipType)"
    }
}