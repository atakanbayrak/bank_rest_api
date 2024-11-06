package org.app.sekom_java_api.configuration.redis;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String HOLDER_CACHE_KEY = "holdersCache";
    public static final String ACCOUNT_CACHE_KEY = "accountsCache";

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Getter
    public static RedisTemplate<String, Object> redisTemplate;
    @PostConstruct
    public void testRedisConnection() {
        try {
            RedisTemplate<String, Object> redisTemplate = redisTemplate(redisConnectionFactory());
            redisTemplate.opsForValue().set("connectionTestKey", "testValue");
            String value = (String) redisTemplate.opsForValue().get("connectionTestKey");
            System.out.println("Redis bağlantısı başarılı! Test değeri: " + value + redisHost);
        } catch (Exception e) {
            System.err.println("Redis bağlantısı başarısız: " + e.getMessage());
        }
    }
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Redis bağlantısı için hostname'i sekom_redis olarak ayarlıyoruz
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);  // Docker Compose'daki Redis hostname
        redisConfig.setPort(redisPort);               // Redis portu
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(HOLDER_CACHE_KEY, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put(ACCOUNT_CACHE_KEY, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        CacheConfig.redisTemplate = redisTemplate;
        return redisTemplate;
    }
}




