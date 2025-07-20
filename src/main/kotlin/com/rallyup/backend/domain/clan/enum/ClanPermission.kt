package com.rallyup.backend.domain.clan.enum

enum class ClanPermission(val requiredLevel: Int) {
    VIEW_CLAN(0),
    PARTICIPATE_MATCHES(0),
    USE_CHAT(0),
    INVITE_MEMBERS(1),
    MODERATE_MEMBERS(2),
    MANAGE_APPLICATIONS(2),
    MANAGE_CLAN(3),
    TRANSFER_LEADERSHIP(4)
}