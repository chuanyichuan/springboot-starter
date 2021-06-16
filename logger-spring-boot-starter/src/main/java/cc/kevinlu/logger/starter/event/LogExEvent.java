package cc.kevinlu.logger.starter.event;

import org.springframework.context.ApplicationEvent;

import cc.kevinlu.logger.starter.entity.ExceptionLog;

/**
 * 异常日志事件
 * 
 * @author chuan
 */
public class LogExEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LogExEvent(ExceptionLog source) {
        super(source);
    }
}
