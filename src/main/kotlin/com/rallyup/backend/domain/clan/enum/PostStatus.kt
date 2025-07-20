package com.rallyup.backend.domain.clan.enum

enum class PostStatus {
    PENDING,    // 검토 대기
    APPROVED,   // 승인됨
    REJECTED,   // 거부됨
    EXPIRED     // 만료됨
}