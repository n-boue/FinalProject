package tsu.finalproject.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String USER_PREFIX = "USER_";
    private final CacheManager cacheManager;

    public void blacklistToken(String jwt) {
        Cache cache = cacheManager.getCache("jwt-blacklist");
        if (cache != null) {
            // arbitrary value
            cache.put(jwt, Boolean.TRUE);
        }
    }

    public boolean isBlacklisted(String jwt) {
        Cache cache = cacheManager.getCache("jwt-blacklist");
        if (cache == null) {
            return false;
        }
        return cache.get(jwt) != null;
    }

    public void blacklistUser(String email) {
        Cache cache = cacheManager.getCache("jwt-blacklist");
        if (cache != null) {
            cache.put(USER_PREFIX + email, Boolean.TRUE);
        }
    }

    public boolean isUserBlacklisted(String email) {
        Cache cache = cacheManager.getCache("jwt-blacklist");
        return cache != null && cache.get(USER_PREFIX + email) != null;
    }
}