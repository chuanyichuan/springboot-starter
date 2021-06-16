package cc.kevinlu.logger.starter.config;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import cc.kevinlu.logger.starter.prop.CcLoggerMailProperties;

/**
 * @author chuan
 */
@Component
public class MailConfig {

    @Resource
    private CcLoggerMailProperties properties;

    public void applyProperties(JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        sender.setDefaultEncoding("utf-8");
        Properties prop = new Properties();
        prop.setProperty("mail.debug", "true");
        prop.setProperty("mail.smtp.socketFactoryClass", "javax.net.ssl.SSLSocketFactory");
        sender.setJavaMailProperties(prop);
    }

    public SimpleMailMessage buildSimpleMessage(String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getUsername());
        message.setTo(properties.getReceivers().toArray(new String[] {}));
        message.setSubject("业务出现异常");
        message.setText(messageBody);
        return message;
    }

}
