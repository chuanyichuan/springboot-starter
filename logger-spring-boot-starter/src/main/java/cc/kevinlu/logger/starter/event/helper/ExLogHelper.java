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
        return getHash(key) + log.hashCode();
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值
     * 
     * @param str
     * @return
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        return Math.abs(hash);
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
