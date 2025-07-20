package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "clan_match_signal_responses",
    indexes = [
        Index(name = "idx_clan_match_signal_responses_signal_id", columnList = "signal_id"),
        Index(name = "idx_clan_match_signal_responses_responder_clan_id", columnList = "responder_clan_id"),
        Index(name = "idx_clan_match_signal_responses_status", columnList = "status"),
        Index(name = "idx_clan_match_signal_responses_deleted_at", columnList = "deleted_at")
    ]
)
class ClanMatchSignalResponse(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = false)
    var signal: ClanMatchSignal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_clan_id", nullable = false)
    var responderClan: Clan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_user_id", nullable = false)
    var responderUser: User, // 응답한 유저

    @Column(name = "message", length = 1000)
    var message: String? = null,

    @Column(name = "proposed_time")
    var proposedTime: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ClanResponseStatus = ClanResponseStatus.PENDING

) : BaseTimeEntity() {

    fun accept() {
        this.status = ClanResponseStatus.ACCEPTED
    }

    fun reject() {
        this.status = ClanResponseStatus.REJECTED
    }

    override fun toString(): String {
        return "ClanMatchSignalResponse(id=$id, signalId=${signal.id}, responderClanId=${responderClan.id}, status=$status)"
    }
}