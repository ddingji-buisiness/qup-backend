package com.rallyup.backend.common.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.core.mapping.RedisMappingContext
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.*
import java.time.Duration

@Configuration
@EnableRedisRepositories(
    basePackages = [
        // 필요시 추가: "com.dding.ddingspring.domain.auth.repository",
        // "com.dding.ddingspring.domain.token.repository"
    ]
)
class RedisConfig {

    private val log = LoggerFactory.getLogger(RedisConfig::class.java)

    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 6379

    @Value("\${spring.data.redis.password}")
    private lateinit var password: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return try {
            // Lettuce 클라이언트 설정
            val clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build()

            // Redis 서버 설정
            val serverConfig = RedisStandaloneConfiguration(host, port).apply {
                if (this@RedisConfig.password.isNotEmpty()) {
                    password = RedisPassword.of(this@RedisConfig.password)
                }
            }

            val factory = LettuceConnectionFactory(serverConfig, clientConfig)
            factory.afterPropertiesSet()

            // 연결 테스트
            factory.connection.ping()
            log.info("Redis 연결 성공: {}:{}", host, port)

            factory
        } catch (e: Exception) {
            log.error("Redis 연결 실패: {}:{} - {}", host, port, e.message, e)
            throw RuntimeException("Redis 연결 실패", e)
        }
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        // ObjectMapper 설정
        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        // JSON Serializer
        val jsonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        // 직렬화 설정
        template.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = jsonSerializer
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = jsonSerializer
            setEnableDefaultSerializer(false)
            setEnableTransactionSupport(false)
            afterPropertiesSet()
        }

        return template
    }

    @Bean
    fun redisKeyValueAdapter(redisTemplate: RedisTemplate<*, *>): RedisKeyValueAdapter {
        return RedisKeyValueAdapter(redisTemplate)
    }

    @Bean
    fun redisKeyValueTemplate(adapter: RedisKeyValueAdapter): RedisKeyValueTemplate {
        return RedisKeyValueTemplate(adapter, redisMappingContext())
    }

    @Bean
    fun redisCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }

    @Bean
    fun redisCacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration())
            .build()
    }

    @Bean
    fun passwordFailCountRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Long> {
        val template = RedisTemplate<String, Long>()
        template.connectionFactory = connectionFactory

        // Long 값을 위한 직렬화 설정
        template.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericToStringSerializer(Long::class.java)
            hashValueSerializer = GenericToStringSerializer(Long::class.java)
            isEnableDefaultSerializer = false
            afterPropertiesSet()
        }

        return template
    }

    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = connectionFactory

        // 직렬화 설정 명시
        template.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
            setEnableDefaultSerializer(false)
            setEnableTransactionSupport(false)
            afterPropertiesSet()
        }

        // 연결 테스트
        try {
            val testKey = "test:connection"
            template.opsForValue().set(testKey, "test", Duration.ofSeconds(5))
            val testValue = template.opsForValue().get(testKey)
            log.info("Redis 연결 테스트 성공 - test value: {}", testValue)
        } catch (e: Exception) {
            log.error("Redis 연결 테스트 실패: {}", e.message, e)
            throw RuntimeException("Redis 연결 실패", e)
        }

        return template
    }

    @Bean
    fun redisMappingContext(): RedisMappingContext {
        return RedisMappingContext()
    }

    // 게임 매칭 서비스 전용 Redis Templates
    @Bean
    fun matchingRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        // 빠른 매칭을 위한 최적화된 직렬화 설정
        template.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()
            isEnableDefaultSerializer = false
            setEnableTransactionSupport(true) // 매칭 로직에서 트랜잭션 필요시
            afterPropertiesSet()
        }

        return template
    }

    @Bean
    fun gameSessionRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory

        // 게임 세션 정보용 (빠른 조회를 위해 String 기반)
        template.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
            isEnableDefaultSerializer = false
            afterPropertiesSet()
        }

        return template
    }

    // 게임별 캐시 설정
    @Bean
    fun gameCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // 게임 세션은 30분
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }

    @Bean
    fun userCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2)) // 사용자 정보는 2시간
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }
}