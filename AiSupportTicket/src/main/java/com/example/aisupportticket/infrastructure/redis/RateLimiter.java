package com.example.aisupportticket.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 限流器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiter {
    
    private final RedissonClient redissonClient;
    
    /** Redis键前缀 */
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    /**
     * 检查是否允许请求
     * 
     * @param key 限流键（如 "api:user:123"）
     * @param limit 时间窗口内允许的最大请求数
     * @param windowSeconds 时间窗口大小（秒）
     * @return 如果允许请求返回true，否则返回false
     */
    public boolean allowRequest(String key, int limit, int windowSeconds) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        
        try {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
            
            rateLimiter.trySetRate(
                RateType.OVERALL,
                limit,
                windowSeconds,
                RateIntervalUnit.SECONDS
            );
            
            return rateLimiter.tryAcquire(1);
        } catch (Exception e) {
            log.error("限流器异常， key: {}", key, e);
            return true;
        }
    }
    
    /**
     * 获取剩余请求数
     * 
     * @param key 限流键
     * @param limit 最大请求数（用于初始化）
     * @return 剩余请求数
     */
    public long getRemainingRequests(String key, int limit) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        
        try {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
            if (!rateLimiter.isExists()) {
                return limit;
            }
            return rateLimiter.availablePermits();
        } catch (Exception e) {
            log.error("获取剩余请求数异常: {}", key, e);
            return limit;
        }
    }
    
    /**
     * 重置限流器
     * 
     * @param key 限流键
     */
    public void reset(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        try {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
            rateLimiter.delete();
        } catch (Exception e) {
            log.error("重置限流器异常: {}", key, e);
        }
    }
}
