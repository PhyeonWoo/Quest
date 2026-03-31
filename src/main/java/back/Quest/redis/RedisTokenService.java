package back.Quest.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final StringRedisTemplate stringRedisTemplate;

    // Refresh Token 저장 (로그인 시 호출)
    public void saveRefreshToken(String id, String refreshToken, long durationMillis) {
        stringRedisTemplate.opsForValue().set(
                "RT:" + id,
                refreshToken,
                durationMillis,
                TimeUnit.MILLISECONDS
        );
        log.info("Redis에 Refresh Token 저장 완료 - User: {}", id);
    }


    public String getRefreshToken(String id) {
        return stringRedisTemplate.opsForValue().get("RT:" + id);
    }


    public void deleteRefreshToken(String id) {
        stringRedisTemplate.delete("RT:" + id);
        log.info("Redis Logout - User: {}", id);
    }

    // Access Token 블랙리스트 등록 (선택사항, 로그아웃 시 호출)
    public void setBlackList(String accessToken, long remainingTimeMillis) {
        stringRedisTemplate.opsForValue().set(
                accessToken,
                "logout",
                remainingTimeMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlackListed(String accessToken) {
        return stringRedisTemplate.hasKey(accessToken);
    }
}