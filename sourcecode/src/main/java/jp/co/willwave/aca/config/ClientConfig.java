package jp.co.willwave.aca.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:config.properties")
public class ClientConfig {
    @Value("${cat.notification.config}")
    private Integer notification;
}
