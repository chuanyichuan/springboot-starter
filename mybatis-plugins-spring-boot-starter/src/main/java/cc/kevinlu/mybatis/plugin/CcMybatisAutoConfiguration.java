package cc.kevinlu.mybatis.plugin;

import java.util.Properties;

import javax.annotation.Resource;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.mybatis.plugin.constants.CcPluginConstants;
import cc.kevinlu.mybatis.plugin.interceptors.UpdateInterceptor;
import cc.kevinlu.mybatis.plugin.prop.CcMybatisPluginProperties;

/**
 * @author chuan
 */
@Configuration
@ConditionalOnClass({ ConfigurationCustomizer.class })
@EnableConfigurationProperties(CcMybatisPluginProperties.class)
@ConditionalOnProperty(value = CcMybatisPluginProperties.PREFIX + ".enabled", havingValue = "true")
public class CcMybatisAutoConfiguration {

    @Resource
    private CcMybatisPluginProperties properties;

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
                UpdateInterceptor interceptor = new UpdateInterceptor();

                Properties prop = new Properties();
                prop.setProperty(CcPluginConstants.THIRD_PART_FIELDS_NAME,
                        JSONObject.toJSONString(properties.getInjectFields()));
                interceptor.setProperties(prop);
                configuration.addInterceptor(interceptor);
            }
        };
    }

}
