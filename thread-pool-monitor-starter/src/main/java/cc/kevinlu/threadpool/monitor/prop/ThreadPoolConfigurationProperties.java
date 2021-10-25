package cc.kevinlu.threadpool.monitor.prop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author chuan
 */
@Data
@ConfigurationProperties(prefix = "cc.monitor.threadpool")
public class ThreadPoolConfigurationProperties {

    private List<ThreadPoolProperties> executors = new ArrayList<>();

}
