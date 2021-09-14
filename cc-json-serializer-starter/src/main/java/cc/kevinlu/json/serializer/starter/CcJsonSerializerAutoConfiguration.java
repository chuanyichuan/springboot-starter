package cc.kevinlu.json.serializer.starter;

import javax.annotation.Resource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cc.kevinlu.json.serializer.starter.prop.CcJsonSerializerProperties;

/**
 * @author chuan
 */
@Configuration
@EnableConfigurationProperties(CcJsonSerializerProperties.class)
public class CcJsonSerializerAutoConfiguration {

    @Resource
    private CcJsonSerializerProperties properties;

}
