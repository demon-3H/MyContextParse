package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.RoleConfig;
import org.example.config.SparkAIConfig;
import org.example.utils.AIUtils.SparkAIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class MyContextParseApplication {

    @Autowired
    private SparkAIConfig sparkAIConfig;
    @Autowired
    private RoleConfig roleConfig;
    public static void main(String[] args) {
        SpringApplication.run(MyContextParseApplication.class, args);
    }

    /**
     * 初始化
     *
     * @return
     */
    @Bean
    public CommandLineRunner init(){
        return args -> {

            SparkAIUtil.init(sparkAIConfig.getAppid(),sparkAIConfig.getApiSecret(),sparkAIConfig.getApiKey());
            roleConfig.init();
            log.info("初始化完成");
        };
    }
}
