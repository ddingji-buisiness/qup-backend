package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.ApplicationWorkflowStage
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "application_workflows")
class ApplicationWorkflow(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    var application: ClanApplication,

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false)
    var currentStage: ApplicationWorkflowStage = ApplicationWorkflowStage.INITIAL_REVIEW
) : BaseTimeEntity() {

    @Column(name = "workflow_history", columnDefinition = "JSON")
    var workflowHistory: String? = null

    @Column(name = "next_action_at")
    var nextActionAt: LocalDateTime? = null

    @Column(name = "assigned_reviewer_id")
    var assignedReviewerId: Long? = null

    @Column(name = "auto_review_eligible", nullable = false)
    var autoReviewEligible: Boolean = false

    @Column(name = "manual_review_required", nullable = false)
    var manualReviewRequired: Boolean = false

    fun moveToNextStage(nextStage: ApplicationWorkflowStage) {
        this.currentStage = nextStage
        this.nextActionAt = calculateNextActionTime(nextStage)
    }

    private fun calculateNextActionTime(stage: ApplicationWorkflowStage): LocalDateTime? {
        return when (stage) {
            ApplicationWorkflowStage.INITIAL_REVIEW -> LocalDateTime.now().plusHours(24)
            ApplicationWorkflowStage.DETAILED_REVIEW -> LocalDateTime.now().plusDays(3)
            ApplicationWorkflowStage.FINAL_DECISION -> LocalDateTime.now().plusDays(1)
            else -> null
        }
    }
}