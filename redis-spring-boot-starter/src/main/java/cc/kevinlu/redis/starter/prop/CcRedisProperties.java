package cc.kevinlu.redis.starter.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chuan
 */
@Configuration
@ConfigurationProperties(prefix = CcRedisProperties.PREFIX)
public class CcRedisProperties {

    public static final String PREFIX = "cc.redis";

    private Boolean            enable = false;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
