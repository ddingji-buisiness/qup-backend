package com.rallyup.backend.domain.clan.enum

enum class VerificationStatus {
    PENDING,    // 승인 대기
    VERIFIED,   // 승인 완료
    REJECTED    // 승인 거부
}