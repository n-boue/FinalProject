package tsu.finalproject.feature.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final CacheManager cacheManager;

    public String createRefreshToken(String userEmail) {
        String token = UUID.randomUUID().toString();
        Cache cache = cacheManager.getCache("refreshTokens");
        if (cache != null) {
            cache.put(token, userEmail);
        }
        return token;
    }

    public String validateAndRotateToken(String token) {
        Cache cache = cacheManager.getCache("refreshTokens");
        if (cache == null) {
            throw new IllegalStateException("Cache uninitialized");
        }

        String userEmail = cache.get(token, String.class);
        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        cache.evict(token);
        return userEmail;
    }

    public void revokeToken(String token) {
        Cache cache = cacheManager.getCache("refreshTokens");
        if (cache != null) {
            cache.evict(token);
        }
    }
}