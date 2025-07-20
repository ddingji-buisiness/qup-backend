package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "application_assessments")
class ApplicationAssessment(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    var application: ClanApplication
) : BaseTimeEntity() {

    @Column(name = "quality_score")
    var qualityScore: Double? = null

    @Column(name = "compatibility_score")
    var compatibilityScore: Double? = null

    @Column(name = "priority_score", nullable = false)
    var priorityScore: Int = 50

    @Column(name = "profile_completeness")
    var profileCompleteness: Double? = null

    @Column(name = "auto_approval_eligible", nullable = false)
    var autoApprovalEligible: Boolean = false
}