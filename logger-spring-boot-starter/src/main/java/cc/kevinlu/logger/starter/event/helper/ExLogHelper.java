package cc.kevinlu.logger.starter.event.helper;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.logger.starter.entity.ExceptionLog;

/**
 * 异常日志Helper
 * 
 * @author chuan
 */
public class ExLogHelper {

    /**
     * 计算日志hash
     * 
     * @param exLog 日志主体
     * @return 日志hash值
     */
    public static int logHash(ExceptionLog exLog) {
        // 计算hash
        String key = exLog.getClazz() + "#" + exLog.getMethod();
        String log = exLog.getMessage();
        return key.hashCode() + log.hashCode();
    }

    public static JSONArray exLogArray(ExceptionLog exLog, String content) {
        JSONArray array = new JSONArray();
        if (StringUtils.isNotBlank(content)) {
            array = JSONArray.parseArray(content);
        }
        if (array.size() > 5) {
            array.remove(0);
        }
        array.add(JSONObject.parseObject(JSONObject.toJSONString(exLog)));
        return array;
    }

}
