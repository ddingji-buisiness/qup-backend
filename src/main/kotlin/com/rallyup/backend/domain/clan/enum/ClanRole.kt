package com.rallyup.backend.domain.clan.enum

enum class ClanRole(val defaultPermissionLevel: Int) {
    LEADER(4),
    OFFICER(3),
    MODERATOR(2),
    MEMBER(1),
    RECRUIT(0)
}

