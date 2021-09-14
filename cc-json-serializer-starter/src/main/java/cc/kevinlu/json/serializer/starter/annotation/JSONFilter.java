package cc.kevinlu.json.serializer.starter.annotation;

import java.lang.annotation.*;

/**
 * 序列化过滤器注解
 * 
 * @author chuan
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONFilter {

    /**
     * 序列化组
     *
     * @return
     */
    String group() default "";

    /**
     * 序列化过滤器处理类,条件满足其一即可
     * 
     * @return
     */
    Class[] conditionClass() default {};

    /**
     * 序列化过滤条件
     * <br/>
     * <p>支持EL表达式</p>
     * <p>与conditionClass同时存在的情况下会被忽略</p>
     * <p>eg:
     * <code> condition = "${spring.profile} == 'dev'"</code>
     * </p>
     * 
     * @return
     */
    String condition() default "";

}
