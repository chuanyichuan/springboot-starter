package cc.kevinlu.json.serializer.starter.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.kevinlu.json.serializer.starter.support.JSONFilterSupport;

/**
 * @author chuan
 */
public class SupportConditionProcessor implements JSONFilterSupport<SupportCondition> {
    private static final Logger log = LoggerFactory.getLogger(SupportConditionProcessor.class);

    @Override
    public boolean serial(SupportCondition supportCondition) {
        Class[] filterClasses = supportCondition.getFilterClass();
        Object entity = supportCondition.getEntity();
        boolean r = false;
        for (Class fc : filterClasses) {
            if (fc.isAssignableFrom(JSONFilterSupport.class)) {
                return false;
            }
            // 条件满足其一即可
            r = r || verifyConditionClass0(fc, entity);
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

}
