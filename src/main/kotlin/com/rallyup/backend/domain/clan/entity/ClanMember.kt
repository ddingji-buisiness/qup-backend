package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ClanPermission
import com.rallyup.backend.domain.clan.enum.ClanRole
import com.rallyup.backend.domain.clan.enum.MemberStatus
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_members",
    indexes = [
        Index(name = "idx_clan_members_clan_status", columnList = "clan_id, status"), // 클랜별 활성 멤버
        Index(name = "idx_clan_members_user_status", columnList = "user_id, status"), // 사용자별 가입 클랜
        Index(name = "idx_clan_members_role", columnList = "role"), // 역할별 조회
        Index(name = "idx_clan_members_joined", columnList = "joined_at"), // 가입일순 조회
        Index(name = "idx_clan_members_activity", columnList = "last_activity_at") // 활동순 조회
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_clan_member_active", columnNames = ["clan_id", "user_id", "status"])
    ]
)
class ClanMember private constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: ClanRole
) : BaseTimeEntity() {

    @Column(name = "nickname", length = 100)
    var nickname: String? = null

    @Column(name = "introduction", length = 1000)
    var introduction: String? = null

    @Column(name = "clan_position", length = 50)
    var clanPosition: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: MemberStatus = MemberStatus.ACTIVE

    @Column(name = "joined_at", nullable = false)
    var joinedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "left_at")
    var leftAt: LocalDateTime? = null

    @Column(name = "last_activity_at")
    var lastActivityAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "permission_level", nullable = false)
    var permissionLevel: Int = 0

    @Column(name = "contribution_score", nullable = false)
    var contributionScore: Int = 0

    @Column(name = "matches_played", nullable = false)
    var matchesPlayed: Int = 0

    @Column(name = "warning_count", nullable = false)
    var warningCount: Int = 0

    @Version
    var version: Long = 0

    fun hasPermission(permission: ClanPermission): Boolean {
        return when {
            !isActive() -> false
            isLeader() -> true
            permissionLevel >= permission.requiredLevel -> true
            else -> false
        }
    }

    fun isActive(): Boolean = status == MemberStatus.ACTIVE
    fun isLeader(): Boolean = role == ClanRole.LEADER
    fun isOfficer(): Boolean = role in listOf(ClanRole.LEADER, ClanRole.OFFICER)

    fun updateActivity() {
        this.lastActivityAt = LocalDateTime.now()
    }

    fun addContribution(points: Int) {
        require(points >= 0) { "기여도 점수는 0 이상이어야 합니다." }
        this.contributionScore += points
        updateActivity()
    }

    override fun toString(): String {
        return "ClanMember(id=$id, clanId=${clan.id}, userId=${user.id}, " +
                "role=$role, status=$status, contribution=$contributionScore)"
    }
}