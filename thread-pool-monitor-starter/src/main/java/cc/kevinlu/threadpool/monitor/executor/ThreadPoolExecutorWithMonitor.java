package cc.kevinlu.threadpool.monitor.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * thread pool executor with monitor
 * 
 * @author chuan
 */
public class ThreadPoolExecutorWithMonitor extends ThreadPoolExecutor {
    /**
     * 默认拒绝策略
     */
    private static final RejectedExecutionHandler DEFAULT_HANDLER   = new AbortPolicy();

    /**
     * 默认线程池名称
     */
    private static final String                   DEFAULT_POOL_NAME = "CC-Monitor-Task";

    public static ThreadFactory                   threadFactory     = new MonitorThreadFactory(DEFAULT_POOL_NAME);

    private long                                  minCostTime;

    private long                                  maxCostTime;

    private AtomicLong                            totalCostTime     = new AtomicLong(0L);

    private ThreadLocal<Long>                     startTimeTl       = new ThreadLocal<>();

    public ThreadPoolExecutorWithMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, DEFAULT_HANDLER);
    }

    public ThreadPoolExecutorWithMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new MonitorThreadFactory(poolName),
                DEFAULT_HANDLER);
    }

    public ThreadPoolExecutorWithMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler,
                                         String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new MonitorThreadFactory(poolName),
                handler);
    }

    public ThreadPoolExecutorWithMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                         RejectedExecutionHandler handler, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimeTl.set(System.currentTimeMillis());
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        long costTime = System.currentTimeMillis() - startTimeTl.get();
        startTimeTl.remove();
        maxCostTime = costTime > maxCostTime ? costTime : maxCostTime;
        if (getCompletedTaskCount() == 0L) {
            minCostTime = costTime;
        }
        minCostTime = costTime < minCostTime ? costTime : minCostTime;
        totalCostTime.getAndAdd(costTime);
        super.afterExecute(r, t);
    }

    public long getMinCostTime() {
        return minCostTime;
    }

    public long getMaxCostTime() {
        return maxCostTime;
    }

    public long getTotalCostTime() {
        return totalCostTime.get();
    }

    public long getAvgCostTime() {
        if (getCompletedTaskCount() == 0L || totalCostTime.get() == 0L) {
            return 0L;
        }
        return totalCostTime.get() / getCompletedTaskCount();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    protected void terminated() {
        super.terminated();
    }

    static class MonitorThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber          = new AtomicInteger(1);

        private final ThreadGroup          group;

        private final AtomicInteger        threadNumber        = new AtomicInteger(1);

        private final String               namePrefix;

        private static final String        NAME_PREFIX_PATTERN = "%s-pool-%s-thread-";

        public MonitorThreadFactory(String poolName) {
            SecurityManager sm = System.getSecurityManager();
            this.group = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = String.format(NAME_PREFIX_PATTERN, poolName, poolNumber.getAndIncrement());
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
