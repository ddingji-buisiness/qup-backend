package com.rallyup.backend.domain.game.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "game_positions",
    indexes = [
        Index(name = "idx_game_positions_game_active", columnList = "game_id, is_active"),
        Index(name = "idx_game_positions_sort", columnList = "sort_order")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_game_position_code", columnNames = ["game_id", "position_code"])
    ]
)
class GamePosition(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game,

    @Column(name = "position_code", length = 50, nullable = false)
    var positionCode: String,

    @Column(name = "position_name", length = 100, nullable = false)
    var positionName: String,

    @Column(name = "sort_order")
    var sortOrder: Int = 0

) : BaseTimeEntity() {

    @Column(name = "icon_url", length = 2048)
    var iconUrl: String? = null

    @Column(name = "description", length = 500)
    var description: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "required_skills", columnDefinition = "JSON")
    var requiredSkills: String? = null

    @Column(name = "difficulty_level")
    var difficultyLevel: Int = 1

    init {
        require(positionCode.isNotBlank()) { "포지션 코드는 필수입니다." }
        require(difficultyLevel in 1..5) { "난이도는 1-5 사이여야 합니다." }
        require(sortOrder >= 0) { "정렬 순서는 0 이상이어야 합니다." }
    }

    override fun toString(): String {
        return "GamePosition(id=$id, gameId=${game.id}, positionCode='$positionCode', positionName='$positionName')"
    }
}