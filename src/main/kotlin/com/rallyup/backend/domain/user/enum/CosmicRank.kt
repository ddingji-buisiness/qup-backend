package com.rallyup.backend.domain.user.enum

enum class CosmicRank(
    val displayName: String,
    val emoji: String,
    val description: String,
    val requiredScore: Int,
    val requiredMatches: Int
) {
    SPACE_CADET(
        "우주 생도", "🚀",
        "우주 여행을 시작한 신입 탐사원",
        0, 0
    ),
    SPACE_EXPLORER(
        "우주 탐사자", "🛸",
        "몇 번의 성공적인 신호 교환을 경험한 탐사자",
        50, 5
    ),
    COSMIC_PILOT(
        "우주 조종사", "👨‍🚀",
        "안정적인 신호 송수신 능력을 갖춘 조종사",
        60, 10
    ),
    SPACE_NAVIGATOR(
        "우주 항해사", "🧭",
        "뛰어난 매칭 센스를 가진 숙련된 항해사",
        70, 20
    ),
    STAR_CAPTAIN(
        "별무리 선장", "⭐",
        "많은 우주인들에게 신뢰받는 선장",
        80, 50
    ),
    GALACTIC_COMMANDER(
        "은하계 사령관", "🌌",
        "우주 신호계의 전설적인 지도자",
        90, 100
    );

    fun getNextRank(): CosmicRank? {
        val currentIndex = entries.indexOf(this)
        return if (currentIndex < entries.size - 1) entries[currentIndex + 1] else null
    }
}