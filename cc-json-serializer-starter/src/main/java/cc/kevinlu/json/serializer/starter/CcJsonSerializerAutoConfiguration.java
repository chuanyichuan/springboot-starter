package cc.kevinlu.json.serializer.starter;

import org.springframework.context.annotation.Configuration;

import cc.kevinlu.json.serializer.starter.prop.CcJsonSerializerProperties;

/**
 * @author chuan
 */
@Configuration
//@EnableConfigurationProperties(CcJsonSerializerProperties.class)
public class CcJsonSerializerAutoConfiguration {

    //    @Resource
    private CcJsonSerializerProperties properties;

}
