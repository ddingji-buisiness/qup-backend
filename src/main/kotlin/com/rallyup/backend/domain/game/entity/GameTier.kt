package com.rallyup.backend.domain.game.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "game_tiers",
    indexes = [
        Index(name = "idx_game_tiers_game_active", columnList = "game_id, is_active"), // 복합 인덱스
        Index(name = "idx_game_tiers_level", columnList = "tier_level")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_game_tier_code", columnNames = ["game_id", "tier_code"]),
        UniqueConstraint(name = "uk_game_tier_level", columnNames = ["game_id", "tier_level"])
    ]
)
class GameTier(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @Column(name = "tier_code", length = 50, nullable = false)
    var tierCode: String,

    @Column(name = "tier_name", length = 100, nullable = false)
    var tierName: String,

    @Column(name = "tier_level", nullable = false)
    var tierLevel: Int

) : BaseTimeEntity() {
    @Column(name = "icon_url", length = 2048)
    var iconUrl: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "description", length = 500)
    var description: String? = null

    @Column(name = "min_mmr")
    var minMMR: Int? = null

    @Column(name = "max_mmr")
    var maxMMR: Int? = null

    @Column(name = "percentile")
    var percentile: Double? = null

    init {
        require(tierLevel >= 0) { "티어 레벨은 0 이상이어야 합니다." }
        require(tierCode.isNotBlank()) { "티어 코드는 필수입니다." }
        percentile?.let { require(it in 0.0..100.0) { "백분율은 0-100 사이여야 합니다." } }
    }

    override fun toString(): String {
        return "GameTier(id=$id, gameId=${game.id}, tierCode='$tierCode', tierName='$tierName', tierLevel=$tierLevel)"
    }
}