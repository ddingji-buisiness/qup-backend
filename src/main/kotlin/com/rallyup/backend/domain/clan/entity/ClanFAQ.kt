package com.rallyup.backend.domain.clan.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.clan.enum.FAQCategory
import jakarta.persistence.*

@Entity
@Table(
    name = "clan_faqs",
    indexes = [
        Index(name = "idx_clan_faqs_clan_active", columnList = "clan_id, is_active"),
        Index(name = "idx_clan_faqs_category", columnList = "category"),
        Index(name = "idx_clan_faqs_sort", columnList = "clan_id, sort_order")
    ]
)
class ClanFAQ(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    var clan: Clan,

    @Column(name = "question", length = 300, nullable = false)
    var question: String,

    @Column(name = "answer", length = 1000, nullable = false)
    var answer: String
) : BaseTimeEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    var category: FAQCategory = FAQCategory.GENERAL

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0
}