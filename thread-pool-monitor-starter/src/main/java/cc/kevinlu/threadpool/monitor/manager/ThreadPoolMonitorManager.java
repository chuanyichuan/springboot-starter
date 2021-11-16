package cc.kevinlu.threadpool.monitor.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cc.kevinlu.threadpool.monitor.exception.NoSuchExecutorException;
import cc.kevinlu.threadpool.monitor.executor.ThreadPoolExecutorWithMonitor;
import cc.kevinlu.threadpool.monitor.prop.ThreadPoolConfigurationProperties;
import cc.kevinlu.threadpool.monitor.prop.ThreadPoolProperties;
import cc.kevinlu.threadpool.monitor.utils.ResizeableBlockingQueue;

/**
 * @author chuan
 */
@Component
public class ThreadPoolMonitorManager {

    @Resource
    private ThreadPoolConfigurationProperties                          poolConfigurationProperties;

    private final ConcurrentMap<String, ThreadPoolExecutorWithMonitor> monitorConcurrentMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (ThreadPoolProperties executor : poolConfigurationProperties.getExecutors()) {
            if (!monitorConcurrentMap.containsKey(executor.getPoolName())) {
                ThreadPoolExecutorWithMonitor executorWithMonitor = new ThreadPoolExecutorWithMonitor(
                        executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getKeepAliveTime(),
                        executor.getTimeUnit(), new ResizeableBlockingQueue<>(executor.getQueueCapacity()),
                        executor.getPoolName());
                monitorConcurrentMap.put(executor.getPoolName(), executorWithMonitor);
            }
        }
    }

    public ThreadPoolExecutorWithMonitor getThreadPoolExecutor(String poolName) {
        ThreadPoolExecutorWithMonitor executorWithMonitor = monitorConcurrentMap.get(poolName);
        if (executorWithMonitor == null) {
            throw new NoSuchExecutorException(poolName);
        }
        return executorWithMonitor;
    }

    public ConcurrentMap<String, ThreadPoolExecutorWithMonitor> getMonitorConcurrentMap() {
        return monitorConcurrentMap;
    }
}
