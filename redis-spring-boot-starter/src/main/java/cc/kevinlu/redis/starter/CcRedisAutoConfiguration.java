package cc.kevinlu.redis.starter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kevinlu.redis.starter.processor.RedisBloomFilterProcessor;
import cc.kevinlu.redis.starter.processor.RedisLockProcessor;
import cc.kevinlu.redis.starter.processor.RedisProcessor;
import cc.kevinlu.redis.starter.prop.CcRedisProperties;

/**
 * @author chuan
 */
@Configuration
@EnableConfigurationProperties(value = CcRedisProperties.class)
@ConditionalOnProperty(value = CcRedisProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class CcRedisAutoConfiguration {

    @Bean(name = "ccRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jacksonSerializer());
        redisTemplate.setHashValueSerializer(jacksonSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public Jackson2JsonRedisSerializer jacksonSerializer() {
        Jackson2JsonRedisSerializer jacksonSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerializer.setObjectMapper(om);
        return jacksonSerializer;
    }

    @Bean
    @ConditionalOnMissingBean(value = RedisProcessor.class)
    public RedisProcessor redisProcessor(@Qualifier(value = "ccRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        RedisProcessor processor = new RedisProcessor();
        processor.setRedisTemplate(redisTemplate);
        return processor;
    }

    @Bean
    @ConditionalOnMissingBean(value = RedisLockProcessor.class)
    public RedisLockProcessor redisLockProcessor(@Qualifier(value = "ccRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        RedisLockProcessor processor = new RedisLockProcessor();
        processor.setRedisTemplate(redisTemplate);
        return processor;
    }

    @Bean
    @ConditionalOnMissingBean(value = RedisBloomFilterProcessor.class)
    public RedisBloomFilterProcessor redisBloomFilterProcessor(@Qualifier(value = "ccRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        RedisBloomFilterProcessor processor = new RedisBloomFilterProcessor();
        processor.setRedisTemplate(redisTemplate);
        return processor;
    }

}
