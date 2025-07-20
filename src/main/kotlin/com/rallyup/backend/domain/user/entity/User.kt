package com.rallyup.backend.domain.user.entity

import com.google.auto.value.AutoValue
import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import com.rallyup.backend.domain.user.enum.MembershipType
import com.rallyup.backend.domain.user.enum.SocialProvider
import com.rallyup.backend.domain.user.enum.SocialProvider.*
import com.rallyup.backend.domain.user.enum.UserStatus
import com.rallyup.backend.domain.user.enum.UserStatus.*
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_email", columnList = "email"),
        Index(name = "idx_users_nickname", columnList = "nickname"),
        Index(name = "idx_users_provider_composite", columnList = "primary_provider, provider_user_id"),
        Index(name = "idx_users_status", columnList = "user_status")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_email", columnNames = ["email"]),
        UniqueConstraint(name = "uk_user_nickname", columnNames = ["nickname"]),
        UniqueConstraint(name = "uk_user_provider", columnNames = ["primary_provider", "provider_user_id"])
    ]
)
@DynamicUpdate
class User private constructor(
    @Column(unique = true, nullable = false, length = 254)
    var email: String,

    @Column(unique = true, nullable = false, length = 100)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_provider", nullable = false)
    var primaryProvider: SocialProvider,

    @Column(name = "provider_user_id", nullable = false, length = 100)
    var providerUserId: String

): BaseTimeEntity() {
    @Column(name = "profile_image_url", length = 2048)
    var profileImageUrl: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 20)
    var status: UserStatus = ACTIVE

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false, length = 20)
    var membershipType: MembershipType = MembershipType.FREE

    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null

    @Column(name = "email_verified", nullable = false)
    var emailVerified: Boolean = false

    @Column(name = "email_verified_at")
    var emailVerifiedAt: LocalDateTime? = null

    fun isActive(): Boolean = status == ACTIVE

    companion object {
        fun create(
            email: String,
            nickname: String,
            provider: SocialProvider,
            providerUserId: String
        ): User {
            require(email.isValidEmail()) { "유효하지 않은 이메일 형식입니다." }
            require(nickname.length in 2..100) { "닉네임은 2-100자 사이여야 합니다." }

            return User(email, nickname, provider, providerUserId)
        }
    }

    fun verifyEmail() {
        this.emailVerified = true
        this.emailVerifiedAt = LocalDateTime.now()
    }

    fun updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now()
    }

    fun updateNickname(newNickname: String) {
        require(newNickname.length in 2..100) { "닉네임은 2-100자 사이여야 합니다." }
        this.nickname = newNickname
    }

    override fun toString(): String {
        return "User(id=$id, email='$email', nickname='$nickname', provider=$primaryProvider, status=$status)"
    }
}

private fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
}
