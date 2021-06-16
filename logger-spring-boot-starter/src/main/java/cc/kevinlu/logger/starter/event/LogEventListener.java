package cc.kevinlu.logger.starter.event;

import javax.annotation.Resource;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.helper.ExLogSupport;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志监控
 * 
 * @author chuan
 */
@Slf4j
@Component
public class LogEventListener {
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource
    private ExLogSupport           exLogSupport;

    /**
     * 通知规则
     * 1. 
     * @param exEvent
     */
    @Async
    @EventListener(LogExEvent.class)
    public void exEventListener(LogExEvent exEvent) {
        // 异步运行异常信息通知任务
        taskExecutor.execute(() -> {
            ExceptionLog exLog = (ExceptionLog) exEvent.getSource();
            exLogSupport.record(exLog);
        });
    }

}
