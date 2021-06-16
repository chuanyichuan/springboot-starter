package cc.kevinlu.logger.starter.annotation.advise;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import cc.kevinlu.logger.starter.annotation.PSlf4jEx;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.LogExEvent;

/**
 * 异常邮件通知
 * 
 * @author chuan
 */
@Aspect
@Component
public class PSlf4jExAdvise implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Pointcut("@annotation(cc.kevinlu.logger.starter.annotation.PSlf4jEx)")
    public void pointcut() {
    }

    /**
     * 定义一个前置通知
     */
    @Before("pointcut()")
    private void before() {
        System.out.println("---前置通知---");
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "ex")
    public void exHandler(JoinPoint point, Throwable ex) {
        PSlf4jEx pSlf4jEx = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(PSlf4jEx.class);
        if (pSlf4jEx == null) {
            // 获取类注解
            pSlf4jEx = point.getTarget().getClass().getAnnotation(PSlf4jEx.class);
        }
        if (pSlf4jEx == null) {
            return;
        }
        // 发送邮件
        ExceptionLog exLog = new ExceptionLog();
        exLog.setClazz(point.getTarget().getClass().getName());
        exLog.setMethod(((MethodSignature) point.getSignature()).getMethod().getName());
        exLog.setMessage(ex.getMessage());
        exLog.setNoticeTimesLimit(pSlf4jEx.times());
        exLog.setHappenTime(LocalDateTime.now());
        this.applicationEventPublisher.publishEvent(new LogExEvent(exLog));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
