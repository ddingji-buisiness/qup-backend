package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.RecruitmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_recruitments",
    indexes = [
        Index(name = "idx_clan_recruitments_clan_status", columnList = "clan_id, status"),
        Index(name = "idx_clan_recruitments_status_expires", columnList = "status, expires_at"),
        Index(name = "idx_clan_recruitments_featured", columnList = "featured_until"),
        Index(name = "idx_clan_recruitments_created", columnList = "created_at")
    ]
)
class ClanRecruitment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "content", length = 2000, nullable = false)
    var content: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime
) : BaseTimeEntity() {

    @Column(name = "required_positions", columnDefinition = "JSON")
    var requiredPositions: String? = null

    @Column(name = "required_tiers", columnDefinition = "JSON")
    var requiredTiers: String? = null

    @Column(name = "slots_available", nullable = false)
    var slotsAvailable: Int = 1

    @Column(name = "requirements", length = 1000)
    var requirements: String? = null

    @Column(name = "benefits", length = 1000)
    var benefits: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: RecruitmentStatus = RecruitmentStatus.ACTIVE

    @Column(name = "featured_until")
    var featuredUntil: LocalDateTime? = null

    @Version
    var version: Long = 0

    @OneToMany(mappedBy = "recruitment", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var applications: MutableList<ClanApplication> = mutableListOf()
        protected set

    override fun toString(): String {
        return "ClanRecruitment(id=$id, clanId=${clan.id}, title='$title', status=$status)"
    }
}