package cc.kevinlu.threadpool.monitor.prop;

import java.util.concurrent.TimeUnit;

import lombok.Data;

/**
 * properties of thread pool
 * 
 * @author chuan
 */
@Data
public class ThreadPoolProperties {

    private String   poolName;

    private int      corePoolSize;

    private int      maximumPoolSize = Runtime.getRuntime().availableProcessors();

    private long     keepAliveTime   = 60;

    private TimeUnit timeUnit        = TimeUnit.SECONDS;

    private int      queueCapacity   = Integer.MAX_VALUE;

}
