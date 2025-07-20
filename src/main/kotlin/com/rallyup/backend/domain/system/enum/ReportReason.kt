package com.rallyup.backend.domain.system.enum

enum class ReportReason {
    HARASSMENT,           // 괴롭힘
    INAPPROPRIATE_CONTENT, // 부적절한 콘텐츠
    SPAM,                 // 스팸
    FAKE_INFORMATION,     // 허위 정보
    TOXICITY,             // 독성 행동
    CHEATING,             // 치팅/부정행위
    IMPERSONATION,        // 사칭
    VIOLATION_OF_RULES,   // 규칙 위반
    OTHER                 // 기타
}