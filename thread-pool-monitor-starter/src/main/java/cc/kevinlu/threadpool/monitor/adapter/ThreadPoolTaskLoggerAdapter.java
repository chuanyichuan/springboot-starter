package cc.kevinlu.threadpool.monitor.adapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import cc.kevinlu.threadpool.monitor.executor.ThreadPoolExecutorWithMonitor;
import cc.kevinlu.threadpool.monitor.manager.ThreadPoolMonitorManager;
import cc.kevinlu.threadpool.monitor.utils.ThreadPoolSpringContextUtils;

/**
 * 线程池任务运行日志
 * 
 * @author chuan
 */
public abstract class ThreadPoolTaskLoggerAdapter {

    protected String             poolName;

    protected ThreadPoolExecutor executor;

    protected Runnable           runnable;

    /**
     * 前置日志记录
     */
    protected abstract void before();

    public void execute(String poolName, Runnable r) {
        this.poolName = poolName;
        this.runnable = r;
        before();
        try {
            getExecutor();
            running();
            this.executor.execute(r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            after();
        }
    }

    public Future<?> submit(String poolName, Runnable r) {
        this.poolName = poolName;
        this.runnable = r;
        before();
        try {
            getExecutor();
            running();
            return this.executor.submit(r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            after();
        }
    }

    /**
     * 获取线程池执行器
     * 
     * @return
     */
    private void getExecutor() {
        if (!StringUtils.hasText(this.poolName)) {
            this.executor = new ThreadPoolExecutorWithMonitor(5, 10, 10, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(20));
        } else {
            this.executor = ThreadPoolSpringContextUtils.getBean(ThreadPoolMonitorManager.class)
                    .getThreadPoolExecutor(this.poolName);
        }
    }

    /**
     * 执行中日志记录
     */
    protected abstract void running();

    /**
     * 后置日志记录
     */
    protected abstract void after();

    /**
     * 日志输出
     * @return 
     */
    public abstract Object printLog();

    /**
     * 日志清除
     * @return 
     */
    public abstract Object clearLog();

}
