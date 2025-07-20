package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ClanMembershipTier
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "clan_subscriptions")
class ClanSubscription(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_tier", nullable = false)
    var membershipTier: ClanMembershipTier
) : BaseTimeEntity() {

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null

    @Column(name = "premium_features", columnDefinition = "JSON")
    var premiumFeatures: String? = null

    @Column(name = "auto_renew", nullable = false)
    var autoRenew: Boolean = false

    @Column(name = "billing_email", length = 254)
    var billingEmail: String? = null

    fun isActive(): Boolean = expiresAt?.isAfter(LocalDateTime.now()) ?: false
}