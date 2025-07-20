package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ClanPermission
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "clan_member_permissions")
class ClanMemberPermission(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: ClanMember,

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    var permission: ClanPermission,

    @Column(name = "granted", nullable = false)
    var granted: Boolean = true
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    var grantedBy: User? = null

    @Column(name = "granted_at", nullable = false)
    var grantedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null

    fun isActive(): Boolean {
        return granted && (expiresAt?.isAfter(LocalDateTime.now()) ?: true)
    }
}