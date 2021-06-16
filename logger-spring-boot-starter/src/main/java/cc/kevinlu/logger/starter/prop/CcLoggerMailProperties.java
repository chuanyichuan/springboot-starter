package cc.kevinlu.logger.starter.prop;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author chuan
 */
@Data
@ConfigurationProperties(prefix = CcLoggerMailProperties.PREFIX)
public class CcLoggerMailProperties {

    public static final String PREFIX = "cc.logger.mail";

    /**
     * 发送者
     */
    private String             sender;

    /**
     * 主机
     */
    private String             host;

    private Integer            port;

    /**
     * 用户名
     */
    private String             username;

    /**
     * 用户密码
     */
    private String             password;

    /**
     * 接收人
     */
    private List<String>       receivers;

    private String             protocol;

}
