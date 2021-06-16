package cc.kevinlu.logger.starter.storage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.alibaba.fastjson.JSONArray;

import cc.kevinlu.logger.starter.annotation.StorageMapping;
import cc.kevinlu.logger.starter.constants.StorageType;
import cc.kevinlu.logger.starter.context.AppContextUtils;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.helper.ExLogHelper;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.strategy.StorageStrategy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis存储器
 *
 * @author chuan
 */
@StorageMapping(StorageType.REDIS)
public class RedisSupport implements StorageStrategy {

    private CcLoggerProperties ccLoggerProperties;

    private JedisPool          jedisPool;

    /**
     * redis中记录异常信息的key
     */
    public static String       EX_KEY = "cc:ex:records";

    @Override
    public void storage(ExceptionLog exLog) {
        Jedis jedis = null;
        try {
            String hash = String.valueOf(ExLogHelper.logHash(exLog));
            jedis = jedisPool.getResource();
            String content = jedis.hget(EX_KEY, hash);
            JSONArray array = ExLogHelper.exLogArray(exLog, content);
            jedis.hset(EX_KEY, hash, array.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedisPool.close();
            }
        }
    }

    @Override
    public void init() {
        this.ccLoggerProperties = AppContextUtils.getBean(CcLoggerProperties.class);
        if (ccLoggerProperties.getStorage() == null) {
            return;
        }
        CcLoggerProperties.StorageEngineProperties storage = ccLoggerProperties.getStorage();
        if (storage.getExtension() != null) {
            EX_KEY = storage.getExtension().getOrDefault("record-key", EX_KEY);
        }
        // init redis template
        initRedisTemplate();
    }

    private void initRedisTemplate() {
        CcLoggerProperties.StorageEngineProperties storage = ccLoggerProperties.getStorage();

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        if (StringUtils.isBlank(storage.getPassword())) {
            jedisPool = new JedisPool(poolConfig, storage.getAddress(), storage.getPort());
        } else {
            jedisPool = new JedisPool(poolConfig, storage.getAddress(), storage.getPort(), 30000,
                    storage.getPassword());
        }
    }

    public void destroy() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
