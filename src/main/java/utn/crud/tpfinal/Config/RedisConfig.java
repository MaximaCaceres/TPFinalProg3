package utn.crud.tpfinal.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import utn.crud.tpfinal.Models.LoginCredential;
import utn.crud.tpfinal.Models.Persona;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, LoginCredential> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, LoginCredential> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<LoginCredential> serializer = new Jackson2JsonRedisSerializer<>(LoginCredential.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
