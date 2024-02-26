package org.example.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * ai接口配置类
 *
 * @author guoshuai
 */
@Configuration
@PropertySource("classpath:sparkai.properties")
@Data
public class SparkAIConfig {
    @Value("${appid}")
    private String appid;
    @Value("${apiSecret}")
    private String apiSecret;
    @Value("${apiKey}")
    private String apiKey;

}
