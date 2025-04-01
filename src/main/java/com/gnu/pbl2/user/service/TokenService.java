package com.gnu.pbl2.user.service;

import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor
@Service
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public void saveToken(Long key, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set("user:" + key + ":token", token, duration, unit);
    }

    public String getToken(Long key) {
        return redisTemplate.opsForValue().get("user:" + key + ":token");
    }

    public void deleteToken(String token) {
        Long userId = jwtUtil.extractUserId(token);
        redisTemplate.delete(String.valueOf(userId));
    }

    public void saveMailToken(String key, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, token, duration, unit);
    }

    public String getMailToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}