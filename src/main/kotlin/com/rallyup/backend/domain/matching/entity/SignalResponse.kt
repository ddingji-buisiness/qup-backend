package com.rallyup.backend.domain.matching.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.matching.dto.DiscordResponseInfo
import com.rallyup.backend.domain.matching.enum.ResponseStatus
import com.rallyup.backend.domain.matching.enum.SourcePlatform
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "signal_responses",
    indexes = [
        Index(name = "idx_signal_responses_signal_id", columnList = "signal_id"),
        Index(name = "idx_signal_responses_responder_id", columnList = "responder_id"),
        Index(name = "idx_signal_responses_status", columnList = "status"),
        Index(name = "idx_signal_responses_responded_at", columnList = "responded_at"),
        Index(name = "idx_signal_responses_processing_priority", columnList = "processing_priority"),
        Index(name = "idx_signal_responses_compatibility_score", columnList = "compatibility_score"),
        Index(name = "idx_signal_responses_source_platform", columnList = "source_platform"),
        Index(name = "idx_signal_responses_deleted_at", columnList = "deleted_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_signal_response",
            columnNames = ["signal_id", "responder_id"]
        )
    ]
)
class SignalResponse(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    var signal: Signal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", nullable = false)
    var responder: User

) : BaseTimeEntity() {
    @Column(name = "message", length = 500)
    var message: String? = null

    @Column(name = "preferred_position", length = 50)
    var preferredPosition: String? = null

    @Column(name = "experience_level", length = 50)
    var experienceLevel: String? = null

    @Column(name = "availability_note", length = 200)
    var availabilityNote: String? = null

    @Column(name = "voice_chat_available", nullable = false)
    var voiceChatAvailable: Boolean = true

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ResponseStatus = ResponseStatus.PENDING
        private set

    @Column(name = "responded_at", nullable = false)
    var respondedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null
        private set

    @Column(name = "processed_by_user_id")
    var processedByUserId: Long? = null
        private set

    @Column(name = "rejection_reason", length = 200)
    var rejectionReason: String? = null
        private set

    @Column(name = "auto_processed", nullable = false)
    var autoProcessed: Boolean = false
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "source_platform", nullable = false, length = 20)
    var sourcePlatform: SourcePlatform = SourcePlatform.WEB

    @Column(name = "discord_user_id", length = 50)
    var discordUserId: String? = null

    @Column(name = "discord_username", length = 100)
    var discordUsername: String? = null

    @Column(name = "discord_message_id", length = 50)
    var discordMessageId: String? = null

    // 매칭 품질 관련
    @Column(name = "compatibility_score")
    var compatibilityScore: Double? = null

    @Column(name = "processing_priority")
    var processingPriority: Int = 0

    @Column(name = "auto_accept_eligible", nullable = false)
    var autoAcceptEligible: Boolean = false

    @Column(name = "manual_review_required", nullable = false)
    var manualReviewRequired: Boolean = false

    @Column(name = "review_reason", length = 200)
    var reviewReason: String? = null

    // 응답 품질 평가
    @Column(name = "response_quality_score")
    var responseQualityScore: Double? = null

    @Column(name = "has_detailed_info", nullable = false)
    var hasDetailedInfo: Boolean = false

    @Column(name = "profile_completeness")
    var profileCompleteness: Double? = null

    @Column(name = "response_time_seconds")
    var responseTimeSeconds: Long? = null

    @Column(name = "processing_deadline")
    var processingDeadline: LocalDateTime? = null

    @Version
    var version: Long = 0

    fun setDiscordInfo(
        discordUserId: String,
        discordUsername: String,
        messageId: String? = null
    ) {
        this.sourcePlatform = SourcePlatform.DISCORD
        this.discordUserId = discordUserId
        this.discordUsername = discordUsername
        this.discordMessageId = messageId
    }

    // 응답 내용 업데이트
    fun updateResponse(
        message: String?,
        preferredPosition: String?,
        experienceLevel: String?,
        availabilityNote: String?,
        voiceChatAvailable: Boolean = true
    ) {
        require(isPending()) { "대기 중인 응답만 수정할 수 있습니다." }

        message?.let {
            require(it.length <= 500) { "메시지는 500자를 초과할 수 없습니다." }
            this.message = it
        }

        this.preferredPosition = preferredPosition
        this.experienceLevel = experienceLevel
        this.availabilityNote = availabilityNote
        this.voiceChatAvailable = voiceChatAvailable

        evaluateResponseQuality()
    }

    // 호환성 점수 설정
    fun setCompatibilityScore(score: Double) {
        require(score in 0.0..100.0) { "호환성 점수는 0-100 사이여야 합니다." }
        this.compatibilityScore = score

        // 자동 수락 대상 여부 결정
        this.autoAcceptEligible = score >= 80.0 && signal.autoAccept

        // 우선순위 설정 (점수가 높을수록 높은 우선순위)
        this.processingPriority = score.toInt()
    }

    // 응답 처리
    fun accept(processedByUserId: Long? = null, autoProcessed: Boolean = false) {
        require(isPending()) { "대기 중인 응답만 수락할 수 있습니다." }

        this.status = ResponseStatus.ACCEPTED
        this.processedAt = LocalDateTime.now()
        this.processedByUserId = processedByUserId
        this.autoProcessed = autoProcessed
    }

    fun reject(
        reason: String? = null,
        processedByUserId: Long? = null,
        autoProcessed: Boolean = false
    ) {
        require(isPending()) { "대기 중인 응답만 거절할 수 있습니다." }

        reason?.let {
            require(it.length <= 200) { "거절 사유는 200자를 초과할 수 없습니다." }
        }

        this.status = ResponseStatus.REJECTED
        this.rejectionReason = reason
        this.processedAt = LocalDateTime.now()
        this.processedByUserId = processedByUserId
        this.autoProcessed = autoProcessed
    }

    fun cancel() {
        require(isPending()) { "대기 중인 응답만 취소할 수 있습니다." }
        this.status = ResponseStatus.CANCELLED
        this.processedAt = LocalDateTime.now()
        this.processedByUserId = responder.id
    }

    fun withdraw() {
        require(isAccepted()) { "수락된 응답만 철회할 수 있습니다." }
        this.status = ResponseStatus.WITHDRAWN
        this.processedAt = LocalDateTime.now()
        this.processedByUserId = responder.id
    }

    // 수동 검토 필요 표시
    fun requireManualReview(reason: String) {
        require(reason.length <= 200) { "검토 사유는 200자를 초과할 수 없습니다." }
        this.manualReviewRequired = true
        this.reviewReason = reason
        this.autoAcceptEligible = false
    }

    // 처리 기한 설정
    fun setProcessingDeadline(deadline: LocalDateTime) {
        this.processingDeadline = deadline
    }

    // 응답 시간 계산 및 설정
    fun calculateResponseTime() {
        this.responseTimeSeconds = java.time.Duration.between(signal.createdAt, respondedAt).seconds
    }

    // 응답 품질 평가
    private fun evaluateResponseQuality() {
        var score = 50.0 // 기본 점수

        // 메시지 작성 여부
        if (!message.isNullOrBlank()) {
            score += 20.0
            if (message!!.length >= 50) score += 10.0 // 상세한 메시지
        }

        // 포지션 선택 여부
        if (!preferredPosition.isNullOrBlank()) score += 15.0

        // 경험 레벨 표시 여부
        if (!experienceLevel.isNullOrBlank()) score += 10.0

        // 가능 시간 명시 여부
        if (!availabilityNote.isNullOrBlank()) score += 5.0

        this.responseQualityScore = score.coerceAtMost(100.0)
        this.hasDetailedInfo = score >= 70.0
    }

    // 프로필 완성도 설정
    fun setProfileCompleteness(completeness: Double) {
        require(completeness in 0.0..100.0) { "프로필 완성도는 0-100 사이여야 합니다." }
        this.profileCompleteness = completeness
    }

    // 상태 확인 메서드들
    fun isPending(): Boolean = status == ResponseStatus.PENDING

    fun isAccepted(): Boolean = status == ResponseStatus.ACCEPTED

    fun isRejected(): Boolean = status == ResponseStatus.REJECTED

    fun isCancelled(): Boolean = status == ResponseStatus.CANCELLED

    fun isWithdrawn(): Boolean = status == ResponseStatus.WITHDRAWN

    fun isProcessed(): Boolean = processedAt != null

    fun canBeModified(): Boolean = isPending()

    fun isFromDiscord(): Boolean = sourcePlatform == SourcePlatform.DISCORD

    fun isHighPriority(): Boolean = processingPriority >= 80

    fun isAutoAcceptReady(): Boolean = autoAcceptEligible && !manualReviewRequired

    fun isOverdue(): Boolean {
        return processingDeadline?.isBefore(LocalDateTime.now()) ?: false
    }

    fun hasGoodQuality(): Boolean = (responseQualityScore ?: 0.0) >= 70.0

    fun isHighCompatibility(): Boolean = (compatibilityScore ?: 0.0) >= 80.0

    // 통계 메서드들
    fun getResponseTime(): Long? = responseTimeSeconds

    fun getProcessingTime(): Long? {
        return processedAt?.let {
            java.time.Duration.between(respondedAt, it).toMinutes()
        }
    }

    fun getWaitingTime(): Long {
        val endTime = processedAt ?: LocalDateTime.now()
        return java.time.Duration.between(respondedAt, endTime).toMinutes()
    }

    fun getOverallScore(): Double {
        val compatScore = compatibilityScore ?: 50.0
        val qualityScore = responseQualityScore ?: 50.0
        val profileScore = profileCompleteness ?: 50.0

        return (compatScore * 0.5 + qualityScore * 0.3 + profileScore * 0.2)
    }

    // Discord 봇용 응답 정보
    fun toDiscordResponseInfo(): DiscordResponseInfo {
        return DiscordResponseInfo(
            responseId = this.id,
            signalId = signal.id,
            responderUserId = responder.id,
            responderUsername = responder.nickname,
            discordUserId = this.discordUserId,
            discordUsername = this.discordUsername,
            preferredPosition = this.preferredPosition,
            message = this.message,
            compatibilityScore = this.compatibilityScore,
            status = this.status.name,
            respondedAt = this.respondedAt
        )
    }

    override fun toString(): String {
        return "SignalResponse(id=$id, signalId=${signal.id}, responderId=${responder.id}, " +
                "status=$status, compatibility=${compatibilityScore}, priority=$processingPriority)"
    }
}