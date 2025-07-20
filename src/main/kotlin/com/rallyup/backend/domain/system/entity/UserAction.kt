package com.rallyup.backend.domain.system.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.system.enum.ActionType
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_actions",
    indexes = [
        Index(name = "idx_user_actions_user_time", columnList = "user_id, action_time"),
        Index(name = "idx_user_actions_type_time", columnList = "action_type, action_time"),
        Index(name = "idx_user_actions_session", columnList = "session_id"),
        Index(name = "idx_user_actions_target", columnList = "target_type, target_id")
    ]
)
class UserAction(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    var actionType: ActionType,

    @Column(name = "action_time", nullable = false)
    var actionTime: LocalDateTime = LocalDateTime.now()
) : BaseTimeEntity() {

    @Column(name = "session_id", length = 100)
    var sessionId: String? = null

    @Column(name = "target_type", length = 30)
    var targetType: String? = null

    @Column(name = "target_id")
    var targetId: Long? = null

    @Column(name = "details", columnDefinition = "JSON")
    var details: String? = null

    @Column(name = "ip_address", length = 45)
    var ipAddress: String? = null

    @Column(name = "user_agent", length = 500)
    var userAgent: String? = null

    @Column(name = "platform", length = 20)
    var platform: String? = null // WEB, MOBILE_APP, DISCORD

    @Column(name = "is_success", nullable = false)
    var isSuccess: Boolean = true

    @Column(name = "error_code")
    var errorCode: String? = null

    @Column(name = "processing_time_ms")
    var processingTimeMs: Long? = null

    companion object {
        fun log(
            user: User,
            actionType: ActionType,
            sessionId: String? = null,
            targetType: String? = null,
            targetId: Long? = null,
            isSuccess: Boolean = true,
            details: String? = null
        ): UserAction {
            return UserAction(user, actionType).apply {
                this.sessionId = sessionId
                this.targetType = targetType
                this.targetId = targetId
                this.isSuccess = isSuccess
                this.details = details
            }
        }
    }

    override fun toString(): String {
        return "UserAction(id=$id, userId=${user.id}, actionType=$actionType, targetType=$targetType)"
    }
}