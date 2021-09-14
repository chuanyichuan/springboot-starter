package cc.kevinlu.logger.starter.annotation;

import java.lang.annotation.*;

/**
 * 存储映射
 * 
 * @author chuan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface StorageMapping {

    /**
     * 存储名称
     */
    String value() default "MySQL";

    /**
     * 是否为主要处理器，一般发生在冲突的情况下
     */
    boolean primary() default false;

}
