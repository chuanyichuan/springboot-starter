package cc.kevinlu.json.serializer.starter.processor;

import java.lang.reflect.Field;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cc.kevinlu.ccstarterdemo.JSONFilterSupport;
import cc.kevinlu.ccstarterdemo.common.Constants;
import cc.kevinlu.ccstarterdemo.utils.SpringEmbeddedValueUtils;

/**
 * @author chuan
 */
public class SimpleConditionProcessor implements JSONFilterSupport<SimpleCondition> {
    private static final Logger log = LoggerFactory.getLogger(SimpleConditionProcessor.class);

    @Override
    public boolean serial(SimpleCondition simpleCondition) {
        String condition = simpleCondition.getCondition();
        Object vo = simpleCondition.getEntity();
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
        if (!condition.matches(Constants.EL_PATTERN)) {
            // 格式不匹配，正常解析
            return verifyCondition0_1(condition, vo);
        }
        // 解析EL
        String[] c0 = condition.split(Constants.EL_OP_PATTERN);
        String p = SpringEmbeddedValueUtils.getProperty(c0[0]);
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
            String[] c0 = condition.split(Constants.EL_OP_PATTERN);
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

}
