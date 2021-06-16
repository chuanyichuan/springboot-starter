package cc.kevinlu.logger.starter.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author chuan
 */
public class AppContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return AppContextUtils.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> type) {
        return AppContextUtils.applicationContext.getBean(type);
    }

}
