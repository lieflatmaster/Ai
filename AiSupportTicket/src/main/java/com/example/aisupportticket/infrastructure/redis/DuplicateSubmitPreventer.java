package com.example.aisupportticket.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 防重复提交器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DuplicateSubmitPreventer {
    
    private final RedissonClient redissonClient;
    
    /** Redis键前缀 */
    private static final String DEDUP_PREFIX = "dedup:";
    
    /**
     * 尝试获取锁
     * 
     * @param key 锁键（如 "order:create:user:123:ORDER001"）
     * @param expireSeconds 锁过期时间（秒）
     * @return 如果成功获取锁返回true，否则返回false
     */
    public boolean tryAcquire(String key, long expireSeconds) {
        String redisKey = DEDUP_PREFIX + key;
        
        try {
            RLock lock = redissonClient.getLock(redisKey);
            return lock.tryLock(0, expireSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断，key: {}", key, e);
            return false;
        } catch (Exception e) {
            log.error("获取锁异常，key: {}", key, e);
            return false;
        }
    }
    
    /**
     * 释放锁
     * 
     * @param key 锁键
     */
    public void release(String key) {
        String redisKey = DEDUP_PREFIX + key;
        
        try {
            RLock lock = redissonClient.getLock(redisKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("释放锁异常，key: {}", key, e);
        }
    }
    
    /**
     * 检查锁是否存在
     * 
     * @param key 锁键
     * @return 如果锁存在返回true，否则返回false
     */
    public boolean exists(String key) {
        String redisKey = DEDUP_PREFIX + key;
        
        try {
            RLock lock = redissonClient.getLock(redisKey);
            return lock.isLocked();
        } catch (Exception e) {
            log.error("检查锁是否存在异常，key: {}", key, e);
            return false;
        }
    }
    
    /**
     * 检查当前线程是否持有锁
     * 
     * @param key 锁键
     * @return 如果当前线程持有锁返回true，否则返回false
     */
    public boolean isHeldByCurrentThread(String key) {
        String redisKey = DEDUP_PREFIX + key;
        
        try {
            RLock lock = redissonClient.getLock(redisKey);
            return lock.isHeldByCurrentThread();
        } catch (Exception e) {
            log.error("检查当前线程是否持有锁异常，key: {}", key, e);
            return false;
        }
    }
}
