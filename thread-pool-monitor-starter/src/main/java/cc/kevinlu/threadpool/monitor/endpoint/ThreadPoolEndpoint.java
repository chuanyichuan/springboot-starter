package cc.kevinlu.threadpool.monitor.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Configuration;

import cc.kevinlu.threadpool.monitor.manager.ThreadPoolMonitorManager;

/**
 * @author chuan
 */
@Configuration
@Endpoint(id = "thread-pool")
public class ThreadPoolEndpoint {

    private static final String      METRIC_NAME = "threadPools";

    @Resource
    private ThreadPoolMonitorManager threadPoolMonitorManager;

    @ReadOperation
    public Map<String, Object> metric() {
        Map<String, Object> metricMap = new HashMap<>();
        List<Map<String, Object>> pools = new ArrayList<>();

        threadPoolMonitorManager.getMonitorConcurrentMap().forEach((k, v) -> {
            Map<String, Object> poolInfo = new HashMap<>(16);
            poolInfo.put("cc.thread.pool.name", k);
            poolInfo.put("cc.thread.pool.core.size", v.getCorePoolSize());
            poolInfo.put("cc.thread.pool.largest.size", v.getLargestPoolSize());
            poolInfo.put("cc.thread.pool.max.size", v.getMaximumPoolSize());
            poolInfo.put("cc.thread.pool.thread.count", v.getPoolSize());
            poolInfo.put("cc.thread.pool.max.costTime", v.getMaxCostTime());
            poolInfo.put("cc.thread.pool.average.costTime", v.getAvgCostTime());
            poolInfo.put("cc.thread.pool.min.costTime", v.getMinCostTime());
            poolInfo.put("cc.thread.pool.active.count", v.getActiveCount());
            poolInfo.put("cc.thread.pool.completed.taskCount", v.getCompletedTaskCount());
            poolInfo.put("cc.thread.pool.queue.name", v.getQueue().getClass().getName());
            poolInfo.put("cc.thread.pool.rejected.name", v.getRejectedExecutionHandler().getClass().getName());
            poolInfo.put("cc.thread.pool.task.count", v.getTaskCount());
            pools.add(poolInfo);
        });
        metricMap.put(METRIC_NAME, pools);
        return metricMap;
    }

}
