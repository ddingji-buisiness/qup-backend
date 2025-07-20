package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_profiles",
    indexes = [
        Index(name = "idx_clan_profiles_clan", columnList = "clan_id"), // 클랜별 프로필
        Index(name = "idx_clan_profiles_updated", columnList = "last_updated_at") // 업데이트 시간
    ]
)
class ClanProfile(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan
) : BaseTimeEntity() {

    @Column(name = "avg_member_level", nullable = false)
    var avgMemberLevel: Double = 0.0

    @Column(name = "most_active_game_id")
    var mostActiveGameId: Long? = null

    @Column(name = "primary_play_time", length = 50)
    var primaryPlayTime: String? = null

    @Column(name = "recruitment_score", nullable = false)
    var recruitmentScore: Double = 0.0

    @Column(name = "clan_personality", columnDefinition = "JSON")
    var clanPersonality: String? = null

    @Column(name = "activity_level", nullable = false)
    var activityLevel: Int = 3

    @Column(name = "competitiveness_level", nullable = false)
    var competitivenessLevel: Int = 3

    @Column(name = "beginner_friendly", nullable = false)
    var beginnerFriendly: Boolean = true

    @Column(name = "total_play_hours")
    var totalPlayHours: Long = 0

    @Column(name = "successful_recruitments", nullable = false)
    var successfulRecruitments: Int = 0

    @Column(name = "total_applications", nullable = false)
    var totalApplications: Int = 0

    @Column(name = "retention_rate", nullable = false)
    var retentionRate: Double = 0.0

    @Column(name = "last_updated_at", nullable = false)
    var lastUpdatedAt: LocalDateTime = LocalDateTime.now()

    @Version
    var version: Long = 0
}