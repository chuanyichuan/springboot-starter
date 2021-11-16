package cc.kevinlu.threadpool.monitor.logging.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.threadpool.monitor.executor.ThreadPoolExecutorWithMonitor;
import cc.kevinlu.threadpool.monitor.logging.ThreadPoolTaskLoggerAdapter;

/**
 * 常规本地缓存日志记录
 * 
 * @author chuan
 */
public class JakartaCommonsLogger extends ThreadPoolTaskLoggerAdapter {

    private static final Map<String, Object> LOG_LOCAL_MAP = new ConcurrentHashMap<>();

    @Override
    protected void before() {
        Long threadId = Thread.currentThread().getId();
        LOG_LOCAL_MAP.put(threadId + "~开始运行:", "------------");
        LOG_LOCAL_MAP.put(threadId + "~线程任务", this.runnable);
        LOG_LOCAL_MAP.put(threadId + "~开始时间", System.currentTimeMillis());
        LOG_LOCAL_MAP.put(threadId + "~线程池名称", this.poolName);
    }

    @Override
    protected void running() {
        Long threadId = Thread.currentThread().getId();
        LOG_LOCAL_MAP.put(threadId + "~运行中:", "------------");
        LOG_LOCAL_MAP.put(threadId + "~线程池执行器", this.executor);
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.name", this.poolName);
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.core.size", this.executor.getCorePoolSize());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.thread.count", this.executor.getPoolSize());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.active.count", this.executor.getActiveCount());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.queue.name", this.executor.getQueue().getClass().getName());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.task.count", this.executor.getTaskCount());
    }

    @Override
    protected void after() {
        Long threadId = Thread.currentThread().getId();
        LOG_LOCAL_MAP.put(threadId + "~运行结束:", "------------");
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.largest.size", this.executor.getLargestPoolSize());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.max.size", this.executor.getMaximumPoolSize());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.completed.taskCount", this.executor.getCompletedTaskCount());
        LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.rejected.name",
                this.executor.getRejectedExecutionHandler().getClass().getName());
        if (this.executor instanceof ThreadPoolExecutorWithMonitor) {
            LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.currentTaskCostTime",
                    ((ThreadPoolExecutorWithMonitor) this.executor).getCurrentTaskCostTime());
            LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.max.costTime",
                    ((ThreadPoolExecutorWithMonitor) this.executor).getMaxCostTime());
            LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.average.costTime",
                    ((ThreadPoolExecutorWithMonitor) this.executor).getAvgCostTime());
            LOG_LOCAL_MAP.put(threadId + "cc.thread.pool.min.costTime",
                    ((ThreadPoolExecutorWithMonitor) this.executor).getMinCostTime());
        }
    }

    @Override
    protected void printLog() {
        System.out.println(JSONObject.toJSONString(LOG_LOCAL_MAP));
    }

    @Override
    protected void clearLog() {
        printLog();
        LOG_LOCAL_MAP.clear();
    }
}
