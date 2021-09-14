package cc.kevinlu.json.serializer.starter.advise;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cc.kevinlu.json.serializer.starter.annotation.JSONFilter;
import cc.kevinlu.json.serializer.starter.processor.SimpleCondition;
import cc.kevinlu.json.serializer.starter.processor.SimpleConditionProcessor;
import cc.kevinlu.json.serializer.starter.processor.SupportCondition;
import cc.kevinlu.json.serializer.starter.processor.SupportConditionProcessor;

/**
 * JSONFilter核心处理器
 *
 * @author chuan
 */
@Aspect
@Component
public class JSONFilterAdvise {
    private static final Logger log = LoggerFactory.getLogger(JSONFilterAdvise.class);

    @Pointcut("@annotation(cc.kevinlu.json.serializer.starter.annotation.JSONFilter)")
    public void pointcut() {
        System.out.println(1);
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void pointcut1() {
        System.out.println(2);
    }

    @AfterReturning(value = "pointcut()", returning = "result")
    private Object after(JoinPoint joinPoint, Object result) {
        // 非List
        if (result instanceof Collection || result instanceof Map) {
            return result;
        }
        Class<?> clazz = result.getClass();
        Field[] fieldArr = clazz.getDeclaredFields();
        if (fieldArr.length == 0) {
            return result;
        }
        for (Field field : fieldArr) {
            field.setAccessible(true);
            if (checkFieldSerializer(field)) {
                continue;
            }
            // 解析并校验condition
            if (!verifySerialCondition(field, result)) {
                try {
                    field.set(result, null);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return result;
    }

    /**
     * 校验字段啊是否需要被序列化
     * @param field
     * @param result
     * @return
     *      true: 持久化
     *      false: 不持久化
     */
    private boolean verifySerialCondition(Field field, Object result) {
        JSONFilter jsonFilter = field.getAnnotation(JSONFilter.class);
        // 检查是否设置了校验类
        Class[] filterClassArr = jsonFilter.conditionClass();
        if (filterClassArr.length == 0) {
            String condition = jsonFilter.condition();
            if (!StringUtils.hasText(condition)) {
                // 没有持久化条件，默认为全部持久化
                return true;
            }
            return verifyCondition(condition, result);
        }
        return verifyConditionClass(filterClassArr, result);
    }

    /**
     * 解析并校验condition
     *
     *  @param condition
     * @param vo
     * @return
     */
    private boolean verifyCondition(String condition, Object vo) {
        SimpleCondition simpleCondition = SimpleCondition.builder().condition(condition).entity(vo).build();
        SimpleConditionProcessor processor = new SimpleConditionProcessor();
        return processor.serial(simpleCondition);
    }

    /**
     * 解析并校验conditionClass
     *
     * @param filterClasses
     * @param vo
     * @return
     */
    private boolean verifyConditionClass(Class[] filterClasses, Object vo) {
        SupportCondition supportCondition = SupportCondition.builder().filterClass(filterClasses).entity(vo).build();
        SupportConditionProcessor processor = new SupportConditionProcessor();
        return processor.serial(supportCondition);
    }

    /**
     * 校验字段是否需要过滤
     *
     * @param field
     * @return
     */
    private boolean checkFieldSerializer(Field field) {
        JSONFilter jsonFilter = field.getAnnotation(JSONFilter.class);
        if (jsonFilter == null) {
            return true;
        }
        Class[] filterClassArr = jsonFilter.conditionClass();
        String condition = jsonFilter.condition();
        if (filterClassArr.length == 0 && !StringUtils.hasText(condition)) {
            // 需要被序列化
            return true;
        }
        // 需要过滤
        return false;
    }
}
