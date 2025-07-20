package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_dna",
    indexes = [
        Index(name = "idx_clan_dna_clan_id", columnList = "clan_id"),
        Index(name = "idx_clan_dna_dominant_gambti", columnList = "dominant_gambti"),
        Index(name = "idx_clan_dna_last_calculated", columnList = "last_calculated"),
        Index(name = "idx_clan_dna_deleted_at", columnList = "deleted_at")
    ]
)
class ClanDNA(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "dominant_gambti", length = 10)
    var dominantGamBTI: String? = null,

    @Column(name = "gambti_distribution", columnDefinition = "JSON")
    var gamBTIDistribution: String? = null,

    @Column(name = "avg_manner_score")
    var avgMannerScore: Double = 50.0,

    @Column(name = "primary_play_time", length = 50)
    var primaryPlayTime: String? = null,

    @Column(name = "competitiveness_score")
    var competitivenessScore: Double = 50.0,

    @Column(name = "social_score")
    var socialScore: Double = 50.0,

    @Column(name = "culture_keywords", columnDefinition = "JSON")
    var cultureKeywords: String? = null,

    @Column(name = "last_calculated")
    var lastCalculated: LocalDateTime = LocalDateTime.now()

) : BaseTimeEntity() {

    fun updateDNA(
        dominantGamBTI: String?,
        gamBTIDistribution: String?,
        avgMannerScore: Double,
        competitivenessScore: Double,
        socialScore: Double,
        cultureKeywords: String?
    ) {
        this.dominantGamBTI = dominantGamBTI
        this.gamBTIDistribution = gamBTIDistribution
        this.avgMannerScore = avgMannerScore
        this.competitivenessScore = competitivenessScore
        this.socialScore = socialScore
        this.cultureKeywords = cultureKeywords
        this.lastCalculated = LocalDateTime.now()
    }

    fun calculateCompatibility(userGamBTI: String): Double {
        return if (dominantGamBTI != null) {
            when {
                dominantGamBTI == userGamBTI -> 100.0
                dominantGamBTI!!.substring(0, 2) == userGamBTI.substring(0, 2) -> 75.0
                dominantGamBTI!!.substring(0, 1) == userGamBTI.substring(0, 1) -> 50.0
                else -> 25.0
            }
        } else 50.0
    }

    fun needsRecalculation(): Boolean {
        return lastCalculated.isBefore(LocalDateTime.now().minusHours(6))
    }

    fun getOverallScore(): Double {
        return (competitivenessScore + socialScore + avgMannerScore) / 3
    }

    override fun toString(): String {
        return "ClanDNA(id=$id, clanId=${clan.id}, dominantGamBTI='$dominantGamBTI', lastCalculated=$lastCalculated)"
    }
}