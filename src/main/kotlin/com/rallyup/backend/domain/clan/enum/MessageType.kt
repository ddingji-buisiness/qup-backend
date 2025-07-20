package com.rallyup.backend.domain.clan.enum

enum class MessageType {
    TEXT,           // 일반 텍스트
    IMAGE,          // 이미지
    FILE,           // 파일
    SYSTEM,         // 시스템 메시지
    MATCH_INVITE,   // 내전 초대
    ANNOUNCEMENT    // 공지
}