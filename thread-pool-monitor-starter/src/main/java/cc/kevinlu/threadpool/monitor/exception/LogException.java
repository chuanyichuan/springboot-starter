package cc.kevinlu.threadpool.monitor.exception;

/**
 * 线程池任务日志记录异常
 * 
 * @author chuan
 */
public class LogException extends RuntimeException {

    public LogException() {
        super();
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }

}
