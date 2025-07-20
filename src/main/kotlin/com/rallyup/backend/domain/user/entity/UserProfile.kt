package com.rallyup.backend.domain.user.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.enum.*
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@Table(
    name = "user_profiles",
    indexes = [
        Index(name = "idx_user_profiles_cosmic_rank", columnList = "cosmic_rank"),
        Index(name = "idx_user_profiles_university", columnList = "university"),
        Index(name = "idx_user_profiles_location", columnList = "location")
    ]
)
@DynamicUpdate
class UserProfile private constructor(
    @Id
    @Column(name = "user_id")
    var userId: Long,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
) : BaseTimeEntity() {

    @Column(name = "university", length = 100)
    var university: String? = null

    @Column(name = "major", length = 100)
    var major: String? = null

    @Column(name = "age")
    var age: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    var gender: Gender? = null

    @Column(name = "location", length = 100)
    var location: String? = null

    @Column(name = "bio", length = 500)
    var bio: String? = null

    @Column(name = "signal_power", nullable = false)
    var signalPower: Int = 50

    @Column(name = "signal_reputation", nullable = false)
    var signalReputation: Double = 50.0

    @Column(name = "response_reliability", nullable = false)
    var responseReliability: Double = 50.0

    @Enumerated(EnumType.STRING)
    @Column(name = "cosmic_rank", nullable = false, length = 20)
    var cosmicRank: CosmicRank = CosmicRank.SPACE_CADET

    @Column(name = "total_matches", nullable = false)
    var totalMatches: Int = 0

    @Column(name = "successful_matches", nullable = false)
    var successfulMatches: Int = 0

    @Column(name = "total_signals_sent", nullable = false)
    var totalSignalsSent: Int = 0

    @Column(name = "total_signals_received", nullable = false)
    var totalSignalsReceived: Int = 0

    companion object {
        fun createForUser(user: User): UserProfile {
            return UserProfile(user.id, user)
        }
    }

    fun updatePersonalInfo(
        university: String? = null,
        major: String? = null,
        age: Int? = null,
        location: String? = null,
        bio: String? = null
    ) {
        university?.let {
            require(it.length <= 100) { "대학명은 100자를 초과할 수 없습니다." }
            this.university = it
        }
        major?.let {
            require(it.length <= 100) { "전공명은 100자를 초과할 수 없습니다." }
            this.major = it
        }
        age?.let {
            require(it in 14..100) { "나이는 14-100 사이여야 합니다." }
            this.age = it
        }
        location?.let {
            require(it.length <= 100) { "지역은 100자를 초과할 수 없습니다." }
            this.location = it
        }
        bio?.let {
            require(it.length <= 500) { "자기소개는 500자를 초과할 수 없습니다." }
            this.bio = it
        }
    }

    override fun toString(): String {
        return "UserProfile(id=$id, userId=${user.id}, signalPower=$signalPower, cosmicRank=$cosmicRank, totalMatches=$totalMatches)"
    }
}
