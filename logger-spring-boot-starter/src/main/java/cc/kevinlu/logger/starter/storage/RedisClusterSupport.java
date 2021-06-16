package cc.kevinlu.logger.starter.storage;

import java.util.HashSet;
import java.util.Set;

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
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * @author chuan
 */
@StorageMapping(StorageType.REDIS_CLUSTER)
public class RedisClusterSupport implements StorageStrategy {

    private CcLoggerProperties ccLoggerProperties;

    private JedisCluster       jedisCluster;

    /**
     * redis中记录异常信息的key
     */
    public static String       EX_KEY = "cc:ex:records";

    @Override
    public void storage(ExceptionLog exLog) {
        try {
            String hash = String.valueOf(ExLogHelper.logHash(exLog));
            String content = jedisCluster.hget(EX_KEY, hash);
            JSONArray array = ExLogHelper.exLogArray(exLog, content);
            jedisCluster.hset(EX_KEY, hash, array.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
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
        String address = storage.getAddress();
        if (StringUtils.isBlank(address)) {
            return;
        }
        String[] hosts = address.split(",");
        Set<HostAndPort> hostSet = new HashSet<>(hosts.length);
        for (String host : hosts) {
            String[] hp = host.split(":");
            hostSet.add(new HostAndPort(hp[0], Integer.parseInt(hp[1])));
        }
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        jedisCluster = new JedisCluster(hostSet, poolConfig);
    }
}
