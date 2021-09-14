package cc.kevinlu.json.serializer.starter.support;

/**
 * jsonfilter 过滤器父类
 * <p>JSONFilter#conditionClass指定的类均需继承于该类</p>
 * 
 * @author chuan
 */
public interface JSONFilterSupport {

    /**
     * 校验字段是否需要持久化
     * 
     * @param t 待校验参数
     * @return
     */
    default boolean serial(Object t) {
        return false;
    }

}
