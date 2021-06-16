package cc.kevinlu.logger.starter.annotation.registrar;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cc.kevinlu.logger.starter.annotation.PSlf4jEx;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.LogExEvent;

/**
 * @author chuan
 */
public class PSlf4jLoggerFactoryBean implements FactoryBean<Object>, ApplicationEventPublisherAware, MethodInterceptor {

    private Class<?>                  type;

    private Object                    target;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object getObject() throws Exception {
        return createObject();
    }

    private Object createObject() {
        Object object = null;
        if (this.type.isInterface() || (this.type.getInterfaces() != null && this.type.getInterfaces().length > 0)) {
            InvocationHandler invocationHandler = (proxy, method, args) -> {
                PSlf4jEx pSlf4jEx = method.getAnnotation(PSlf4jEx.class);
                PSlf4jEx pSlf4jExType = this.type.getAnnotation(PSlf4jEx.class);
                if (pSlf4jEx == null && pSlf4jExType == null) {
                    return method.invoke(this.target, args);
                }
                try {
                    return method.invoke(this.target, args);
                } catch (Throwable e) {
                    // 发布日志邮件通知
                    ExceptionLog log = new ExceptionLog();
                    log.setClazz(this.type.getName());
                    log.setMethod(method.getName());
                    log.setMessage(e.getMessage());
                    applicationEventPublisher.publishEvent(new LogExEvent(log));
                    throw e;
                }
            };
            object = Proxy.newProxyInstance(this.type.getClassLoader(), this.type.getInterfaces(), invocationHandler);
        } else {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(this.type);
            enhancer.setCallback(this);
            object = enhancer.create();
        }
        return object;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        PSlf4jEx pSlf4jEx = method.getAnnotation(PSlf4jEx.class);
        PSlf4jEx pSlf4jExType = this.type.getAnnotation(PSlf4jEx.class);
        if (pSlf4jEx == null && pSlf4jExType == null) {
            return method.invoke(o, objects);
        }
        try {
            return method.invoke(o, objects);
        } catch (Throwable e) {
            // 发布日志邮件通知
            ExceptionLog log = new ExceptionLog();
            log.setClazz(this.type.getName());
            log.setMethod(method.getName());
            log.setMessage(e.getMessage());
            applicationEventPublisher.publishEvent(new LogExEvent(log));
            throw e;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
