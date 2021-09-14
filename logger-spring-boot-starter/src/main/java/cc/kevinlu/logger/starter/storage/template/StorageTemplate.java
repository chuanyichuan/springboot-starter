package cc.kevinlu.logger.starter.storage.template;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import cc.kevinlu.logger.starter.annotation.StorageMapping;
import cc.kevinlu.logger.starter.config.MailConfig;
import cc.kevinlu.logger.starter.constants.LoggerConstants;
import cc.kevinlu.logger.starter.constants.StorageType;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.helper.ExLogHelper;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.strategy.StorageStrategy;
import cc.kevinlu.redis.starter.processor.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 持久化模板
 * 
 * @author chuan
 */
@Slf4j
public class StorageTemplate implements InitializingBean {

    @Autowired
    private CcLoggerProperties ccLoggerProperties;
    @Autowired
    private RedisProcessor     redisProcessor;
    @Resource
    private JavaMailSender     exMailSender;
    @Resource
    private MailConfig         mailConfig;

    /**
     * 日志存储类型，默认为MySQL
     */
    private static String      STORAGE_TYPE = StorageType.MYSQL;

    /**
     * 获取所有的存储策略
     * 
     * @return 存储策略
     */
    private StorageStrategy scanStrategy() {
        StorageStrategy storageStrategy = null;
        ServiceLoader<StorageStrategy> serviceLoader = ServiceLoader.load(StorageStrategy.class);
        Iterator<StorageStrategy> it = serviceLoader.iterator();
        while (it.hasNext()) {
            try {
                StorageStrategy strategy = it.next();
                StorageMapping mapping = strategy.getClass().getAnnotation(StorageMapping.class);
                if (mapping == null) {
                    continue;
                }
                String key = mapping.value();
                if (STORAGE_TYPE.equals(key)) {
                    boolean primary = mapping.primary();
                    if (primary) {
                        // 只能强制替换一次
                        storageStrategy = strategy;
                        break;
                    }
                    storageStrategy = strategy;
                }
            } catch (Exception e) {
            }
        }
        // 初始化，可能会报错，报错就报错
        if (storageStrategy != null) {
            storageStrategy.init();
        }
        return storageStrategy;
    }

    /**
     * 记录异常日志
     * 
     * @param exLog 日志主体
     */
    public void record(ExceptionLog exLog) {
        StorageStrategy storageStrategy = scanStrategy();
        // 记录日志
        storageStrategy.storage(exLog);
        // 判断是否需要通知
        // 计算hash
        int hash = ExLogHelper.logHash(exLog);
        // 获取hash的通知次数
        Object item = redisProcessor.hget(LoggerConstants.EX_NOTICE_TIMES_KEY, String.valueOf(hash));
        if (item != null && Integer.parseInt(String.valueOf(item)) >= exLog.getNoticeTimesLimit()) {
            // 不发送通知
            return;
        }
        // 发送通知
        // 构建通知
        exMailSender.send(mailConfig.buildSimpleMessage(exLog.getMessage()));
        // 计数器+1
        redisProcessor.hincr(LoggerConstants.EX_NOTICE_TIMES_KEY, String.valueOf(hash), 1.0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化存储策略
        if (ccLoggerProperties.getStorage() != null
                && StringUtils.isNotBlank(ccLoggerProperties.getStorage().getType())) {
            STORAGE_TYPE = ccLoggerProperties.getStorage().getType();
        }
    }
}
