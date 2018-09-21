package jp.co.willwave.aca.config;

import jp.co.willwave.aca.model.constant.Constant;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:mailcontentconfig.properties", encoding = Constant.UTF_8)
public class ContentMail {

    @Value("${reset.password.subject}")
    private String resetPasswordSubject;
    @Value("${reset.password.template.file.path}")
    private String resetPasswordTemplateFilePath;
}
