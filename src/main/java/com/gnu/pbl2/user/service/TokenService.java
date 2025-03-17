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

    public void saveToken(String key, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, token, duration, unit);
    }

    public String getToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }

}