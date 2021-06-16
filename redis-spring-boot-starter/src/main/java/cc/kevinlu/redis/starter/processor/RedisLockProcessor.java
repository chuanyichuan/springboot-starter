package cc.kevinlu.redis.starter.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cc
 */
@Slf4j
public class RedisLockProcessor {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 定义获取锁的lua脚本
     */
    public static final DefaultRedisScript<Long>  LOCK_LUA_SCRIPTS  = new DefaultRedisScript<>(
            "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call"
                    + "('pexpire',KEYS[1],ARGV[2]) else return 0 end end",
            Long.class);

    /**
     * 定义释放锁的lua脚本
     */
    private final static DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end",
            Long.class);

    /**
     * 使用lua语句实现分布式锁
     *
     * @param key
     * @param value
     * @param times 毫秒
     * @return
     */
    public boolean tryLockWithLua(String key, String value, int times) {
        try {
            List<String> values = new ArrayList<>(2);
            values.add(value);
            values.add(String.valueOf(times));

            Long result = redisTemplate.execute(LOCK_LUA_SCRIPTS, Collections.singletonList(key), value, times);

            //判断是否成功
            return Objects.equals(result, 1L);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 使用set key value nx ex实现分布式锁
     *
     * @param key
     * @param value
     * @param times
     * @return
     */
    public boolean tryLockWithSet(String key, String value, int times) {
        return tryLockWithSet(key, value, times, TimeUnit.MILLISECONDS);
    }

    /**
     * 使用set key value nx ex实现分布式锁
     *
     * @param key
     * @param value
     * @param times
     * @param timeUnit
     * @return
     */
    public boolean tryLockWithSet(String key, String value, int times, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, times, timeUnit);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock(String key, String value) {
        try {
            String v = (String) redisTemplate.opsForValue().get(key);
            if (!value.equals(v)) {
                return false;
            }
            Long result = redisTemplate.execute(UNLOCK_LUA_SCRIPT, Collections.singletonList(key), value);
            //判断是否成功
            return Objects.equals(result, 1L);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

}
