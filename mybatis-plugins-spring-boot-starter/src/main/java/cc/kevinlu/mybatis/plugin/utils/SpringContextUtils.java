package cc.kevinlu.mybatis.plugin.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        return SpringContextUtils.applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanName) {
        return (T) SpringContextUtils.applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return SpringContextUtils.applicationContext.getBean(name, clazz);
    }

}
