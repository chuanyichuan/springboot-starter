package cc.kevinlu.logger.starter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import cc.kevinlu.logger.starter.annotation.advise.PSlf4jExAdvise;
import cc.kevinlu.logger.starter.config.ElasticsearchConfig;
import cc.kevinlu.logger.starter.config.MailConfig;
import cc.kevinlu.logger.starter.constants.LoggerConstants;
import cc.kevinlu.logger.starter.context.AppContextUtils;
import cc.kevinlu.logger.starter.event.LogEventListener;
import cc.kevinlu.logger.starter.event.helper.ExLogSupport;
import cc.kevinlu.logger.starter.prop.CcLoggerMailProperties;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.template.StorageTemplate;
import cn.hutool.core.net.NetUtil;

/**
 * @author chuan
 */
@Configuration
@EnableConfigurationProperties({ CcLoggerProperties.class, CcLoggerMailProperties.class })
@ConditionalOnProperty(value = CcLoggerProperties.PREFIX + ".enabled", havingValue = "true")
@EnableAspectJAutoProxy
public class CcLoggerAutoConfiguration {

    @Resource
    private CcLoggerProperties properties;

    public static String       clientIp = null;

    @PostConstruct
    public void postConstruct() {
        if (properties.getLocation()) {
            // 获取IP
            clientIp = NetUtil.getLocalhostStr();
        }
    }

    @Bean
    public AppContextUtils appContextUtils() {
        return new AppContextUtils();
    }

    @Bean
    public MailConfig mailConfig() {
        return new MailConfig();
    }

    @Bean
    @ConditionalOnProperty(name = CcLoggerProperties.PREFIX + ".storage.type", havingValue = "ElasticSearch")
    public ElasticsearchConfig elasticsearchConfig() {
        return new ElasticsearchConfig();
    }

    @Bean(name = "exMailSender")
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        mailConfig().applyProperties(sender);
        return sender;
    }

    @Bean
    public PSlf4jExAdvise advise() {
        return new PSlf4jExAdvise();
    }

    @Bean
    public LogEventListener listener() {
        return new LogEventListener();
    }

    @Bean
    public ExLogSupport exLogSupport() {
        return new ExLogSupport();
    }

    @Bean
    public StorageTemplate storageTemplate() {
        return new StorageTemplate();
    }

    @Bean("applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(LoggerConstants.POOL_EXECUTOR);
        return multicaster;
    }

}
