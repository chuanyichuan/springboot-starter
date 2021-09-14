package cc.kevinlu.logger.starter.annotation;

import java.lang.annotation.*;

/**
 * 异常通知开关
 * 
 * @author chuan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface PSlf4jEx {

    /**
     * 通知次数，默认为3次
     */
    int times() default 3;

}
