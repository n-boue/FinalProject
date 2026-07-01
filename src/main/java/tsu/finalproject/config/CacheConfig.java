package tsu.finalproject.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("{application.security.jwt.expiration}")
    private long jwtExpiration;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("refreshTokens",
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(7, TimeUnit.DAYS)
                        .build());
        cacheManager.registerCustomCache("jwt-blacklist",
                Caffeine.newBuilder()
                        .maximumSize(20_000)
                        .expireAfterWrite(jwtExpiration, TimeUnit.MILLISECONDS)
                        .build());

        cacheManager.registerCustomCache("active-semesters",
                Caffeine.newBuilder().maximumSize(10).expireAfterWrite(1, TimeUnit.HOURS).build());
        cacheManager.registerCustomCache("semesters",
                Caffeine.newBuilder().maximumSize(50).expireAfterWrite(1, TimeUnit.HOURS).build());

        cacheManager.registerCustomCache("courses",
                Caffeine.newBuilder().maximumSize(500).expireAfterWrite(15, TimeUnit.MINUTES).build());
        cacheManager.registerCustomCache("course-details",
                Caffeine.newBuilder().maximumSize(500).expireAfterWrite(15, TimeUnit.MINUTES).build());
        cacheManager.registerCustomCache("professor-sessions",
                Caffeine.newBuilder().maximumSize(500).expireAfterWrite(30, TimeUnit.MINUTES).build());


        return cacheManager;
    }
}