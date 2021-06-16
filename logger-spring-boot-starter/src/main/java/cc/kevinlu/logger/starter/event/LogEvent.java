package cc.kevinlu.logger.starter.event;

import org.springframework.context.ApplicationEvent;

import cc.kevinlu.logger.starter.entity.CommonLog;

/**
 * @author chuan
 */
public class LogEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LogEvent(CommonLog source) {
        super(source);
    }
}
