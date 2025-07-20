package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.*
import com.rallyup.backend.domain.clan.enum.ClanStatus.*
import com.rallyup.backend.domain.clan.enum.JoinType.*
import com.rallyup.backend.domain.game.entity.Game
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clans",
    indexes = [
        Index(name = "idx_clans_status_recruiting", columnList = "status, is_recruiting"), // 활성 모집 조회
        Index(name = "idx_clans_game_type", columnList = "primary_game_id, clan_type"), // 게임별 조회
        Index(name = "idx_clans_region_lang", columnList = "region, primary_language"), // 지역별 조회
        Index(name = "idx_clans_ranking", columnList = "ranking_score"), // 랭킹 조회
        Index(name = "idx_clans_verification", columnList = "is_verified, verification_level") // 검증된 클랜 조회
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_clans_name", columnNames = ["name"]),
        UniqueConstraint(name = "uk_clans_tag", columnNames = ["clan_tag"])
    ]
)
class Clan private constructor(
    @Column(unique = true, nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    var leader: User,

    @Column(name = "description", length = 2000, nullable = false)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "clan_type", nullable = false, length = 20)
    var clanType: ClanType
) : BaseTimeEntity() {

    @Column(name = "display_name", length = 100, nullable = false)
    var displayName: String = name

    @Column(name = "clan_tag", unique = true, length = 10)
    var clanTag: String? = null

    @Column(name = "short_description", length = 200)
    var shortDescription: String? = null

    @Column(name = "logo_url", length = 2048)
    var logoUrl: String? = null

    @Column(name = "banner_url", length = 2048)
    var bannerUrl: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_game_id")
    var primaryGame: Game? = null

    @Column(name = "supported_games", columnDefinition = "JSON")
    var supportedGames: String? = null

    @Column(name = "max_members", nullable = false)
    var maxMembers: Int = 50

    @Column(name = "current_members", nullable = false)
    var currentMembers: Int = 1

    @Column(name = "region", length = 100)
    var region: String? = null

    @Column(name = "primary_language", length = 10)
    var primaryLanguage: String? = null

    @Column(name = "timezone", length = 50)
    var timezone: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ClanStatus = ACTIVE

    @Enumerated(EnumType.STRING)
    @Column(name = "join_type", nullable = false, length = 20)
    var joinType: JoinType = APPLICATION

    @Column(name = "is_recruiting", nullable = false)
    var isRecruiting: Boolean = true

    @Column(name = "is_verified", nullable = false)
    var isVerified: Boolean = false

    @Column(name = "verification_level", nullable = false)
    var verificationLevel: Int = 0

    @Column(name = "ranking_score", nullable = false)
    var rankingScore: Int = 0

    @Column(name = "total_matches", nullable = false)
    var totalMatches: Int = 0

    @Column(name = "total_wins", nullable = false)
    var totalWins: Int = 0

    @Column(name = "reputation_score", nullable = false)
    var reputationScore: Int = 50

    @Column(name = "clan_rules", length = 5000)
    var clanRules: String? = null

    @Column(name = "auto_approve_applications", nullable = false)
    var autoApproveApplications: Boolean = false

    @Column(name = "require_voice_chat", nullable = false)
    var requireVoiceChat: Boolean = false

    @Column(name = "minimum_age")
    var minimumAge: Int? = null

    @Column(name = "maximum_age")
    var maximumAge: Int? = null

    @Column(name = "last_activity_at")
    var lastActivityAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "founded_at", nullable = false)
    var foundedAt: LocalDateTime = LocalDateTime.now()

    @Version
    var version: Long = 0

    @OneToMany(mappedBy = "clan", fetch = FetchType.LAZY)
    var members: MutableList<ClanMember> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "clan", fetch = FetchType.LAZY)
    var gameRankings: MutableList<ClanRanking> = mutableListOf()
        protected set

    companion object {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 100
        const val MIN_DESCRIPTION_LENGTH = 10
        const val MAX_DESCRIPTION_LENGTH = 2000
        const val DEFAULT_MAX_MEMBERS = 50
        const val MAX_MEMBERS_LIMIT = 200

        fun create(
            name: String,
            leader: User,
            description: String,
            clanType: ClanType,
            primaryGame: Game? = null
        ): Clan {
            require(name.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH) {
                "클랜명은 $MIN_NAME_LENGTH-$MAX_NAME_LENGTH 자 사이여야 합니다."
            }
            require(description.length in MIN_DESCRIPTION_LENGTH..MAX_DESCRIPTION_LENGTH) {
                "설명은 $MIN_DESCRIPTION_LENGTH-$MAX_DESCRIPTION_LENGTH 자 사이여야 합니다."
            }

            return Clan(name, leader, description, clanType).apply {
                this.primaryGame = primaryGame
                this.displayName = name
            }
        }
    }

    fun updateBasicInfo(displayName: String, description: String, shortDescription: String? = null) {
        require(displayName.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH) {
            "표시명은 $MIN_NAME_LENGTH-$MAX_NAME_LENGTH 자 사이여야 합니다."
        }
        require(description.length in MIN_DESCRIPTION_LENGTH..MAX_DESCRIPTION_LENGTH) {
            "설명은 $MIN_DESCRIPTION_LENGTH-$MAX_DESCRIPTION_LENGTH 자 사이여야 합니다."
        }

        this.displayName = displayName
        this.description = description
        this.shortDescription = shortDescription
        updateActivity()
    }

    private fun updateActivity() {
        this.lastActivityAt = LocalDateTime.now()
    }

    override fun toString(): String {
        return "Clan(id=$id, name='$name', displayName='$displayName', " +
                "members=$currentMembers/$maxMembers, status=$status, verified=$isVerified)"
    }
}