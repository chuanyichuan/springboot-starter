package cc.kevinlu.json.serializer.starter.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chuan
 */
@Component
@ConfigurationProperties(prefix = CcJsonSerializerProperties.PREFIX)
public class CcJsonSerializerProperties {
    protected static final String PREFIX = "cc.json";

    private boolean               format = false;

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }
}
