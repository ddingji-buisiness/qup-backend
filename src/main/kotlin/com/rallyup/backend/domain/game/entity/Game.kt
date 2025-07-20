package com.rallyup.backend.domain.game.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.game.enum.GameStatus
import jakarta.persistence.*

@Entity
@Table(
    name = "games",
    indexes = [
        Index(name = "idx_games_status_featured", columnList = "status, featured"), // 복합 인덱스
        Index(name = "idx_games_sort_order", columnList = "sort_order")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_games_name", columnNames = ["name"]),
        UniqueConstraint(name = "uk_games_code", columnNames = ["game_code"])
    ]
)
class Game private constructor(
    @Column(unique = true, nullable = false, length = 100)
    var name: String,

    @Column(name = "display_name", length = 100, nullable = false)
    var displayName: String,

    @Column(name = "game_code", unique = true, length = 50, nullable = false)
    var gameCode: String
) : BaseTimeEntity() {

    @Column(name = "icon_url", length = 2048)
    var iconUrl: String? = null

    @Column(name = "background_url", length = 2048)
    var backgroundUrl: String? = null

    @Column(name = "min_team_size", nullable = false)
    var minTeamSize: Int = 2

    @Column(name = "max_team_size", nullable = false)
    var maxTeamSize: Int = 6

    @Column(name = "has_ranked_system", nullable = false)
    var hasRankedSystem: Boolean = true

    @Column(name = "api_supported", nullable = false)
    var apiSupported: Boolean = false

    @Column(name = "api_endpoint", length = 2048)
    var apiEndpoint: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: GameStatus = GameStatus.ACTIVE

    @Column(name = "description", length = 1000)
    var description: String? = null

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0

    @Column(name = "featured", nullable = false)
    var featured: Boolean = false

    @Column(name = "player_count", nullable = false)
    var playerCount: Long = 0

    @Version
    var version: Long = 0

    companion object {
        fun create(name: String, displayName: String, gameCode: String): Game {
            require(name.length in 2..100) { "게임명은 2-100자 사이여야 합니다." }
            require(gameCode.matches(Regex("^[A-Z0-9_]+$"))) { "게임 코드는 대문자, 숫자, 언더스코어만 가능합니다." }

            return Game(name, displayName, gameCode)
        }
    }

    override fun toString(): String {
        return "Game(id=$id, name='$name', gameCode='$gameCode', status=$status)"
    }
}