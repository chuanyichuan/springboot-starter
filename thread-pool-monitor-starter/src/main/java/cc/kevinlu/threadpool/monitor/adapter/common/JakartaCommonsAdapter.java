package cc.kevinlu.threadpool.monitor.adapter.common;

import java.util.LinkedList;

import cc.kevinlu.threadpool.monitor.adapter.ThreadPoolTaskLoggerAdapter;
import cc.kevinlu.threadpool.monitor.executor.ThreadPoolExecutorWithMonitor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 常规本地缓存日志记录
 *
 * @author chuan
 */
public class JakartaCommonsAdapter extends ThreadPoolTaskLoggerAdapter {

    private static final LinkedList<Log> LOG_LINKED_DB          = new LinkedList<>();

    /**
     * 默认日志存储最大容量
     */
    private static final Long            DEFAULT_STORE_MAX_SIZE = 5000L;

    /**
     * 日志存储最大容量
     */
    private Long                         maxSize                = DEFAULT_STORE_MAX_SIZE;

    @Override
    protected void before() {
        put("开始运行:", "------------");
        put("线程任务", this.runnable);
        put("开始时间", System.currentTimeMillis());
        put("线程池名称", this.poolName);
    }

    @Override
    protected void running() {
        put("运行中:", "------------");
        put("线程池执行器", this.executor);
        put("cc.thread.pool.name", this.poolName);
        put("cc.thread.pool.core.size", this.executor.getCorePoolSize());
        put("cc.thread.pool.thread.count", this.executor.getPoolSize());
        put("cc.thread.pool.active.count", this.executor.getActiveCount());
        put("cc.thread.pool.queue.name", this.executor.getQueue().getClass().getName());
        put("cc.thread.pool.task.count", this.executor.getTaskCount());
    }

    @Override
    protected void after() {
        put("运行结束:", "------------");
        put("cc.thread.pool.largest.size", this.executor.getLargestPoolSize());
        put("cc.thread.pool.max.size", this.executor.getMaximumPoolSize());
        put("cc.thread.pool.completed.taskCount", this.executor.getCompletedTaskCount());
        put("cc.thread.pool.rejected.name", this.executor.getRejectedExecutionHandler().getClass().getName());
        if (this.executor instanceof ThreadPoolExecutorWithMonitor) {
            put("cc.thread.pool.currentTaskCostTime",
                    ((ThreadPoolExecutorWithMonitor) this.executor).getCurrentTaskCostTime());
            put("cc.thread.pool.max.costTime", ((ThreadPoolExecutorWithMonitor) this.executor).getMaxCostTime());
            put("cc.thread.pool.average.costTime", ((ThreadPoolExecutorWithMonitor) this.executor).getAvgCostTime());
            put("cc.thread.pool.min.costTime", ((ThreadPoolExecutorWithMonitor) this.executor).getMinCostTime());
        }
        put("~结束时间", System.currentTimeMillis());
    }

    /**
     * 存储日志
     * 
     * @param key
     * @param value
     */
    private void put(String key, Object value) {
        cleanup();
        Long threadId = Thread.currentThread().getId();
        Log log = new Log(threadId + "~" + key, value);
        LOG_LINKED_DB.addLast(log);
    }

    /**
     * 校验日志容量是否已满，允许5以内的差错
     */
    private void cleanup() {
        if (LOG_LINKED_DB.size() > this.maxSize - 5) {
            for (int i = 0; i < 5; i++) {
                LOG_LINKED_DB.removeFirst();
            }
        }
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void printLog() {
        System.out.println(LOG_LINKED_DB);
    }

    @Override
    public void clearLog() {
        printLog();
        LOG_LINKED_DB.clear();
    }

    @Data
    @AllArgsConstructor
    private static class Log {
        private String key;
        private Object value;

        @Override
        public String toString() {
            return key + " : " + value + "\n";
        }
    }
}
