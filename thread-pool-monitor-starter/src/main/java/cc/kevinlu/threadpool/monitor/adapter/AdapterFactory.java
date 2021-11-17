package cc.kevinlu.threadpool.monitor.adapter;

import java.lang.reflect.Constructor;

import cc.kevinlu.threadpool.monitor.adapter.common.JakartaCommonsAdapter;
import cc.kevinlu.threadpool.monitor.exception.LogException;

/**
 * 日志记录工厂
 * 
 * @author chuan
 */
public class AdapterFactory {

    private static Constructor<? extends ThreadPoolTaskLoggerAdapter> logConstructor;

    private static Class<? extends ThreadPoolTaskLoggerAdapter>       adapterClass;

    private static ThreadPoolTaskLoggerAdapter                        loggerAdapter;

    public static ThreadPoolTaskLoggerAdapter getLog() {
        if (loggerAdapter != null) {
            return loggerAdapter;
        }
        return getLog(JakartaCommonsAdapter.class);
    }

    public static ThreadPoolTaskLoggerAdapter getLog(Class<? extends ThreadPoolTaskLoggerAdapter> implClass) {
        try {
            if (adapterClass != null && implClass.getName().equals(adapterClass.getName())) {
                return loggerAdapter;
            }
            setImplementation(implClass);
            return loggerAdapter;
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger [" + logConstructor + "].  Cause: " + t, t);
        }
    }

    private static void setImplementation(Class<? extends ThreadPoolTaskLoggerAdapter> implClass) {
        try {
            adapterClass = implClass;
            logConstructor = implClass.getConstructor();
            loggerAdapter = logConstructor.newInstance();
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
