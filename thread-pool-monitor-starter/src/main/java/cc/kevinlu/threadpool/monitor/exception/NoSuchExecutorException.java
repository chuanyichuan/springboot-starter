package cc.kevinlu.threadpool.monitor.exception;

/**
 * @author chuan
 */
public class NoSuchExecutorException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "找不到名字为[%s]的线程池!";

    public NoSuchExecutorException() {
    }

    public NoSuchExecutorException(String poolName) {
        super(String.format(DEFAULT_MESSAGE, poolName));
    }
}
