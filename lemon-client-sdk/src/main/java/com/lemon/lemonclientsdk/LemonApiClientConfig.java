package com.lemon.lemonclientsdk;

import com.lemon.lemonclientsdk.client.LemonApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//将该类标记为配置类，能够读取application.yml的配置，把读到的配置设置到我们这里的属性中
@Configuration
//给所有的配置加上前缀 lemon-api.client
@ConfigurationProperties("lemon-api.client")
@Data
//自动扫描组件，使spring能自动注册相应的bean
@ComponentScan
public class LemonApiClientConfig {

    private String accessKey;
    private String secretKey;

    //创建一个名为lemonApiClient的bean
    @Bean
    public LemonApiClient lemonApiClient(){
        return new LemonApiClient(accessKey,secretKey);
    }
}
