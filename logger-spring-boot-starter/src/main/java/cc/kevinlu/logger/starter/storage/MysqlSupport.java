package cc.kevinlu.logger.starter.storage;

import cc.kevinlu.logger.starter.annotation.StorageMapping;
import cc.kevinlu.logger.starter.constants.StorageType;
import cc.kevinlu.logger.starter.context.AppContextUtils;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.strategy.StorageStrategy;

/**
 * MySQL存储器
 * 
 * @author chuan
 */
@StorageMapping(StorageType.MYSQL)
public class MysqlSupport implements StorageStrategy {

    private CcLoggerProperties ccLoggerProperties;

    @Override
    public void init() {
        this.ccLoggerProperties = AppContextUtils.getBean(CcLoggerProperties.class);
    }

    @Override
    public void storage(ExceptionLog exLog) {
        System.out.println("MYSQL");
    }
}
