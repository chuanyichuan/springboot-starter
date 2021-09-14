package cc.kevinlu.json.serializer.starter.advise;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import cc.kevinlu.json.serializer.starter.annotation.JSONFilter;
import cc.kevinlu.json.serializer.starter.support.JSONFilterSupport;

/**
 * JSONFilter核心处理器
 *
 * @author chuan
 */
@Aspect
@Component
public class JSONFilterAdvise implements ApplicationContextAware, EnvironmentAware, EmbeddedValueResolverAware {

    private static final Logger log           = LoggerFactory.getLogger(JSONFilterAdvise.class);

    private ApplicationContext  applicationContext;

    private Environment         environment;

    public static final String  EL_PATTERN    = "^\\s*\\$\\{\\w+(.|_|-)*\\w*\\}\\s*(>|<|!|=)=?\\s*('|\")?\\w*('|\")?\\s*";
    public static final String  EL_OP_PATTERN = "(>|<|!|=)=?";

    private StringValueResolver stringValueResolver;

    @Pointcut("@annotation(cc.kevinlu.json.serializer.starter.annotation.JSONFilter)")
    public void pointcut() {
        System.out.println(1);
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void pointcut1() {
        System.out.println(2);
    }

    @AfterReturning(value = "pointcut() || pointcut1()", returning = "result")
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
        // 校验condition条件
        if (!(condition.contains("&&") || condition.contains("||"))) {
            return verifyCondition0(condition, vo);
        }
        return verifyCondition1(condition, vo);
    }

    /**
     * 普通型单条件校验
     *
     * @param condition
     * @param vo
     * @return
     */
    private boolean verifyCondition0(String condition, Object vo) {
        if (!condition.matches(EL_PATTERN)) {
            // 格式不匹配，正常解析
            return verifyCondition0_1(condition, vo);
        }
        // 解析EL
        String[] c0 = condition.split(EL_OP_PATTERN);
        String p = getProperty(c0[0]);
        String c1 = trim(c0[1]);
        return !Objects.equals(p, c1);
    }

    /**
     * 解析当前类
     *
     * @param condition
     * @param vo
     * @return
     */
    private boolean verifyCondition0_1(String condition, Object vo) {
        try {
            String[] c0 = condition.split(EL_OP_PATTERN);
            String item = c0[0].trim();
            Field f = vo.getClass().getDeclaredField(item);
            f.setAccessible(true);
            String fv = String.valueOf(f.get(vo));
            String c2 = trim(c0[1]);
            return !Objects.equals(fv, c2);
        } catch (Exception e) {
            log.warn("[{}] serial error!", condition, e);
            return true;
        }
    }

    private String trim(String s) {
        if (StringUtils.hasText(s)) {
            s = s.trim().replaceAll("'", "").replaceAll("\"", "");
        }
        return s;
    }

    /**
     * 组合型校验
     *
     * @param condition
     * @param vo
     * @return
     */
    private boolean verifyCondition1(String condition, Object vo) {
        return false;
    }

    /**
     * 解析并校验conditionClass
     *
     * @param filterClasses
     * @param vo
     * @return
     */
    private boolean verifyConditionClass(Class[] filterClasses, Object vo) {
        boolean r = false;
        for (Class fc : filterClasses) {
            if (fc.isAssignableFrom(JSONFilterSupport.class)) {
                return false;
            }
            // 条件满足其一即可
            r = r || verifyConditionClass0(fc, vo);
        }
        return r;
    }

    private boolean verifyConditionClass0(Class fc, Object vo) {
        try {
            JSONFilterSupport support = (JSONFilterSupport) fc.newInstance();
            return support.serial(vo);
        } catch (Exception e) {
            log.warn("[{}] serial error!", fc, e);
            return false;
        }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    public String getProperty(String name) {
        return StringUtils.trimAllWhitespace(this.stringValueResolver.resolveStringValue(name));
    }
}
