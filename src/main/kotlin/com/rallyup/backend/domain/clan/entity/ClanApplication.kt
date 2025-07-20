package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ApplicationStatus
import com.rallyup.backend.domain.clan.enum.ApplicationWorkflowStage
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_applications",
    indexes = [
        Index(name = "idx_clan_applications_clan_status", columnList = "clan_id, status"), // 클랜별 지원 조회
        Index(name = "idx_clan_applications_applicant", columnList = "applicant_id, status"), // 지원자별 조회
        Index(name = "idx_clan_applications_recruitment", columnList = "recruitment_id"), // 모집공고별 조회
        Index(name = "idx_clan_applications_applied", columnList = "applied_at"), // 지원일순 조회
        Index(name = "idx_clan_applications_expires", columnList = "expires_at") // 만료일 조회
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_active_application", columnNames = ["clan_id", "applicant_id", "status"])
    ]
)
class ClanApplication private constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    var applicant: User,

    @Column(name = "application_message", length = 1000, nullable = false)
    var applicationMessage: String
) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    var recruitment: ClanRecruitment? = null

    @Column(name = "preferred_position", length = 50)
    var preferredPosition: String? = null

    @Column(name = "experience_description", length = 2000)
    var experienceDescription: String? = null

    @Column(name = "voice_chat_available", nullable = false)
    var voiceChatAvailable: Boolean = true

    @Column(name = "age")
    var age: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ApplicationStatus = ApplicationStatus.PENDING

    @Column(name = "applied_at", nullable = false)
    var appliedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    var reviewedBy: User? = null

    @Column(name = "review_message", length = 1000)
    var reviewMessage: String? = null

    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null

    @Version
    var version: Long = 0

    companion object {
        const val MIN_MESSAGE_LENGTH = 10
        const val MAX_MESSAGE_LENGTH = 1000
        const val DEFAULT_EXPIRY_DAYS = 30L

        fun create(
            clan: Clan,
            applicant: User,
            applicationMessage: String,
            recruitment: ClanRecruitment? = null
        ): ClanApplication {
            require(applicationMessage.length in MIN_MESSAGE_LENGTH..MAX_MESSAGE_LENGTH) {
                "지원 메시지는 $MIN_MESSAGE_LENGTH - $MAX_MESSAGE_LENGTH 자 사이여야 합니다."
            }

            return ClanApplication(clan, applicant, applicationMessage).apply {
                this.recruitment = recruitment
                this.expiresAt = LocalDateTime.now().plusDays(DEFAULT_EXPIRY_DAYS)
            }
        }
    }
}