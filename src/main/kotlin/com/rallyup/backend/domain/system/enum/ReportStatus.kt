package com.rallyup.backend.domain.system.enum

enum class ReportStatus {
    PENDING,        // 대기
    UNDER_REVIEW,   // 검토 중
    RESOLVED,       // 해결됨
    DISMISSED,      // 기각됨
    ESCALATED       // 에스컬레이션
}