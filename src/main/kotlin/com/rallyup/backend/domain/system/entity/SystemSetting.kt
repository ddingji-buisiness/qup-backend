package com.rallyup.backend.domain.system.entity

import com.rallyup.backend.domain.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "system_settings",
    indexes = [
        Index(name = "idx_system_settings_category", columnList = "category"),
        Index(name = "idx_system_settings_environment", columnList = "environment"),
        Index(name = "idx_system_settings_updated", columnList = "updated_at")
    ]
)
class SystemSetting(
    @Id
    @Column(name = "setting_key", length = 100)
    var key: String,

    @Column(name = "setting_value", length = 2000, nullable = false)
    var value: String
) : BaseTimeEntity() {

    @Column(name = "category", length = 50, nullable = false)
    var category: String = "general"

    @Column(name = "environment", length = 20, nullable = false)
    var environment: String = "production" // development, staging, production

    @Column(name = "description", length = 500)
    var description: String? = null

    @Column(name = "data_type", length = 20, nullable = false)
    var dataType: String = "string" // string, number, boolean, json

    @Column(name = "is_sensitive", nullable = false)
    var isSensitive: Boolean = false

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = false // 클라이언트에서 접근 가능한지

    @Column(name = "validation_rule", length = 500)
    var validationRule: String? = null

    @Column(name = "default_value", length = 2000)
    var defaultValue: String? = null

    fun updateValue(newValue: String, validate: Boolean = true) {
        if (validate) {
            validateValue(newValue)
        }
        this.value = newValue
    }

    private fun validateValue(newValue: String) {
        when (dataType) {
            "number" -> newValue.toDoubleOrNull() ?: throw IllegalArgumentException("숫자 형식이 아닙니다.")
            "boolean" -> if (newValue !in listOf("true", "false")) throw IllegalArgumentException("boolean 값이 아닙니다.")
            "json" -> {
                try {
                    // JSON 유효성 검사 (실제로는 JSON 라이브러리 사용)
                } catch (e: Exception) {
                    throw IllegalArgumentException("유효하지 않은 JSON 형식입니다.")
                }
            }
        }
    }

    override fun toString(): String {
        return "SystemSetting(key='$key', value='$value')"
    }
}