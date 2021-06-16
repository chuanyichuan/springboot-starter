package cc.kevinlu.logger.starter.prop;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cc.kevinlu.logger.starter.constants.StorageType;
import lombok.Data;

/**
 * @author chuan
 */
@Data
@ConfigurationProperties(prefix = CcLoggerProperties.PREFIX)
public class CcLoggerProperties {

    public static final String      PREFIX   = "cc.logger";

    /**
     * 是否开启记录
     */
    private Boolean                 enabled  = false;

    /**
     * 服务名称
     */
    private String                  serviceName;

    /**
     * 是否需要定位IP
     */
    private Boolean                 location = false;

    /**
     * 持久化配置
     */
    private StorageEngineProperties storage;

    @Data
    public static class StorageEngineProperties {

        /**
         * 持久化，默认支持<code>StorageType</code>中的持久化方式，
         * 但也支持自定义，但必须继承<code>StorageType</code>
         * @see StorageType
         */
        private String              type;

        /**
         * 地址,可以是集群ip,也可以是连接串
         */
        private String              address;

        /**
         * 端口,可为空
         */
        private int                 port;

        /**
         * 连接用户名
         */
        private String              username;

        /**
         * 连接用户密码
         */
        private String              password;

        /**
         * 扩展信息
         */
        private Map<String, String> extension;

    }

}
