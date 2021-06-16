package cc.kevinlu.logger.starter.event.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.storage.template.StorageTemplate;

/**
 * @author chuan
 */
@Component
public class ExLogSupport {

    @Autowired
    private StorageTemplate storageTemplate;

    public void record(ExceptionLog exLog) {
        // 装饰器
        storageTemplate.record(exLog);
    }

}
