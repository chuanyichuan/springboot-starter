package cc.kevinlu.logger.starter.constants;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chuan
 */
public class LoggerConstants {

    /**
     * 全局traceID名称
     */
    public static final String             GLOBAL_TRACE_ID     = "chainTraceId";

    public static final ThreadPoolExecutor POOL_EXECUTOR       = new ThreadPoolExecutor(1, 2, 20L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), r -> {
                                                                           Thread thread = new Thread(r);
                                                                           thread.setName(
                                                                                   "p-slf4j-pool-" + thread.getName());
                                                                           return thread;
                                                                       });

    public static final String             EX_NOTICE_TIMES_KEY = "tool:ex:notice:times";

}
