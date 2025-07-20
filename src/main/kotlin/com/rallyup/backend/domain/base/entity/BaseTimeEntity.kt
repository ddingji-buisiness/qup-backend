package com.rallyup.backend.domain.base.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L
        protected set

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun restore() {
        this.deletedAt = null
    }

    fun isDeleted(): Boolean = deletedAt != null

    fun isNew(): Boolean = id == 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as BaseTimeEntity

        if (id == 0L || other.id == 0L) {
            return false
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        return if (id == 0L) 0 else id.hashCode()
    }

    override fun toString(): String {
        return "${this::class.simpleName}(id=$id)"
    }
}