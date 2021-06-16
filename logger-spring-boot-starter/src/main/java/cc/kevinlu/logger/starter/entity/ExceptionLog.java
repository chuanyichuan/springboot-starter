package cc.kevinlu.logger.starter.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author chuan
 */
@Data
public class ExceptionLog implements Serializable {

    private String        traceId;

    private String        clazz;

    private String        method;

    private String        message;

    private Integer       noticeTimesLimit;

    private LocalDateTime happenTime;

}
