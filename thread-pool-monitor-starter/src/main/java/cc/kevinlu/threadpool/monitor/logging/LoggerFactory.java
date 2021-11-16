package cc.kevinlu.threadpool.monitor.logging;

import java.lang.reflect.Constructor;

import cc.kevinlu.threadpool.monitor.exception.LogException;
import cc.kevinlu.threadpool.monitor.logging.common.JakartaCommonsLogger;

/**
 * 日志记录工厂
 * 
 * @author chuan
 */
public class LoggerFactory {

    private static Constructor<? extends ThreadPoolTaskLoggerAdapter> logConstructor;

    static {
        tryImplementation(LoggerFactory::useCommonsLogging);
    }

    public static ThreadPoolTaskLoggerAdapter getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static ThreadPoolTaskLoggerAdapter getLog(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    public static synchronized void useCommonsLogging() {
        setImplementation(JakartaCommonsLogger.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
            }
        }
    }

    private static void setImplementation(Class<? extends ThreadPoolTaskLoggerAdapter> implClass) {
        try {
            Constructor<? extends ThreadPoolTaskLoggerAdapter> candidate = implClass.getConstructor();
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
