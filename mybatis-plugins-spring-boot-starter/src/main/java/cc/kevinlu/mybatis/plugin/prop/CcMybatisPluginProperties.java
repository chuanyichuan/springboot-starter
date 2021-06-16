package cc.kevinlu.mybatis.plugin.prop;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <pre>
 * cc:
 *   mybatis:
 *     plugin:
 *       enabled: true
 *       inject-fields:
 *         gmtCreated: INSERT,java.util.Date,1
 *         gmtUpdated: INSERT|UPDATE,java.util.Date,1
 * </pre>
 * 
 * @author chuan
 */
@Data
@ConfigurationProperties(prefix = CcMybatisPluginProperties.PREFIX)
public class CcMybatisPluginProperties {

    public static final String  PREFIX  = "cc.mybatis.plugin";

    /**
     * 自动注入属性
     */
    private Map<String, String> injectFields;

    /**
     * 是否开启，默认为关闭
     */
    private Boolean             enabled = false;

}
