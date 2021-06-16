package cc.kevinlu.logger.starter.storage.strategy;

import cc.kevinlu.logger.starter.entity.ExceptionLog;

/**
 * 存储策略
 * 
 * @author chuan
 */
public interface StorageStrategy {

    /**
     * 初始化参数
     * 
     * @param appContextUtils
     */
    void init();

    /**
     * 日志存储，依据不同的存储器进行相关存储逻辑
     * 
     * @param exLog 异常日志
     */
    void storage(ExceptionLog exLog);

}
